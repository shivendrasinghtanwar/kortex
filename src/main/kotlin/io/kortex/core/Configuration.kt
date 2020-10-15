package io.kortex.core

import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.kortex.core.utils.LoggerUtils.Companion.errorLog
import io.kortex.core.utils.LoggerUtils.Companion.success
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory


object Configuration{
  private val logger = LoggerFactory.getLogger(this::class.java)
  //holds configuration of application in JSON object
  lateinit var config: JsonObject
  lateinit var environment: String
  lateinit var fileUploadsDir: String
  lateinit var mongoConfig: JsonObject
  lateinit var mailConfig: JsonObject
  private const val configFilePath = "src/config/config.json"

  fun init(environment: String):Promise<Void>{
    logger.info(enter())
    val promise = Promise.promise<Void>()
    if(::config.isInitialized) config = JsonObject()
    this.environment = environment

    val envOptions = ConfigStoreOptions().setType("env")
    val fileStoreOptions = ConfigStoreOptions().setType("file")
    fileStoreOptions.config = JsonObject().put("path", configFilePath)

    val options = ConfigRetrieverOptions().setStores(listOf(envOptions, fileStoreOptions))

    val configRetriever = ConfigRetriever.create(vertx, options)

    configRetriever.getConfig { result->
      if(result.succeeded()) {
        //default config is picked up from production tag
        val defaultConf = result.result().getJsonObject("local")

        //then provided environment tag is merged with default config
        //for e.g. suppose apiUrl = production.url in 'production' config
        //and you want to change this for local environment
        //just create entry under 'local' tag i.e. apiUrl = local.url
        //and provide 'local' while calling init and local url will take precedence over production url
        val envSpecificConf = if(environment=="local") JsonObject() else result.result().getJsonObject(environment)
        when {
          envSpecificConf==null -> promise.fail("No configuration found for given environment --> $environment")
          defaultConf==null -> promise.fail("local conf cannot be null. Review your conf file --> $configFilePath")
          else -> {
            defaultConf.map.putAll(envSpecificConf.map)
            config = defaultConf
            initializeBasicMembers()
            logger.info(success())
            promise.complete()
          }
        }
      }else{
        promise.fail("Maybe check your configuration file at the path? --> $configFilePath")
      }
    }
    return promise
  }

  private fun initializeBasicMembers(){
    logger.info(enter())
    try{
      fileUploadsDir = config.getString("fileUploadsDir")
      mongoConfig = config.getJsonObject("mongodb")
      mailConfig = config.getJsonObject("mail")
    }catch (e:Exception){
      logger.error(errorLog(e))
    }
  }
}

package io.kortex.core

import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.kortex.core.utils.LoggerUtils.Companion.logException
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
  const val configFilePath = "src/config/config.json"

  fun init():Promise<Void>{
    logger.info(enter())
    val promise = Promise.promise<Void>()
    if(::config.isInitialized) config = JsonObject()

    val envOptions = ConfigStoreOptions().setType("env")
    val fileStoreOptions = ConfigStoreOptions().setType("file")
    fileStoreOptions.config = JsonObject().put("path", configFilePath)

    val options = ConfigRetrieverOptions().setStores(listOf(envOptions, fileStoreOptions))

    val configRetriever = ConfigRetriever.create(vertx, options)

    configRetriever.getConfig { result->
      if(result.succeeded()) {

        val rawConfig = result.result()

        if(rawConfig.containsKey("env")){
          environment = rawConfig.getString("env")

          when (val envSpecificConf = result.result().getJsonObject(environment)) {
              null -> promise.fail("No configuration found for given environment --> $environment")
              else -> {
                config = envSpecificConf
                logger.info("Retrieved Configuration --")
                logger.info(config.encodePrettily())
                initializeBasicMembers()
                logger.info(success())
                promise.complete()
              }
          }

        }
        else promise.fail("Environment Not found!!")
      }else{
        promise.fail(result.cause())
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
      logger.error(logException(e))
    }
  }
}

package io.kortex.core.dataServices

import io.kortex.core.Configuration
import io.kortex.core.enums.CollectionNames
import io.kortex.core.enums.DataBusMessages
import io.kortex.core.utils.LoggerUtils.Companion.deployed
import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.kortex.core.utils.LoggerUtils.Companion.errorLog
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.UpdateOptions
import org.slf4j.LoggerFactory

open class DataService: AbstractVerticle() {
  lateinit var mongo: MongoClient
  open val logger = LoggerFactory.getLogger(this::class.java)
  open val collection = CollectionNames.Default

  override fun start(){
    mongo = MongoClient.createShared(vertx, Configuration.mongoConfig)
    val bus = vertx.eventBus()

    bus.consumer<JsonObject>(DataBusMessages.Insert.into(collection)) { insert(it) }
    bus.consumer<JsonObject>(DataBusMessages.Find.from(collection)) { find(it) }
    bus.consumer<String>(DataBusMessages.FindById.from(collection)) { findById(it) }
    bus.consumer<String>(DataBusMessages.DeleteById.from(collection)) { deleteById(it) }
    bus.consumer<JsonObject>(DataBusMessages.UpdateById.from(collection)) { updateById(it) }
    bus.consumer<JsonObject>(DataBusMessages.Count.from(collection)) { count(it) }
    bus.consumer<JsonObject>(DataBusMessages.UpdateMultipleByIds.from(collection)) {updateMultipleByIds(it)}
    logger.info(deployed(this::class.java.name))
  }

  /**
   * Inserts one record in collection
   * @param message
   */
  private fun insert(message: Message<JsonObject>){
    logger.info(enter())
    mongo.insert(collection.name, message.body()) { res ->
      if(res.succeeded()) {
        message.reply(res.result())
      } else {
        logger.error(errorLog(res.cause()))
        message.fail(1, res.cause().message)
      }
    }
  }
  /**
   * Finds list of records from collection matching the condition
   * @param message
   */
  private fun find(message: Message<JsonObject>) {
    logger.info(enter())
    val options = FindOptions()
    if (message.body().containsKey("limit")) {
      options.limit = message.body().getInteger("limit")
    }
    if (message.body().containsKey("skip")) {
      options.skip = message.body().getInteger("skip")
    }
    if ((message.body().containsKey("sortfield") && message.body().containsKey("sortdir"))) {
      val field = message.body().getString("sortfield")
      val dir = message.body().getString("sortdir").toInt()
      options.sort = JsonObject().put(field, dir)
    }

    val query = if (message.body().containsKey("query")) message.body().getJsonObject("query") else JsonObject()

    mongo.findWithOptions(collection.name, query, options) { h ->
      if (h.succeeded()) message.reply(JsonArray(h.result()))
      else {
        logger.error(errorLog(h.cause()))
        message.fail(1, h.cause().message)
      }
    }
  }
  /**
   * Finds one record from collection matching the ID (mongo generated)
   * @param message
   */
  private fun findById(message: Message<String>) {
    logger.info(enter())
    val query = JsonObject().put("_id",message.body())

    mongo.findOne(collection.name, query, null){
      if(it.succeeded()){
        message.reply(it.result())
      }else{
        message.fail(1,it.cause().message)
      }
    }
  }
  /**
   * Deletes one record from collection matching the ID (mongo generated)
   * @param message
   */
  private fun deleteById(message: Message<String>) {
    logger.info(enter())
    val query = JsonObject().put("_id", message.body())
    mongo.removeDocument(collection.name, query) { deleteRecord ->
      if (deleteRecord.succeeded()) message.reply(deleteRecord.result().removedCount)
      else {
        logger.error(errorLog(deleteRecord.cause()))
        message.fail(1,deleteRecord.cause().message)
      }
    }
  }
  /**
   * Updates one record from collection matching the ID (mongo generated)
   * @param message
   */
  private fun updateById(message: Message<JsonObject>){
    logger.info(enter())

    val query = JsonObject().put("_id", JsonObject().put("\$oid",message.body().getString("id")))
    val update = JsonObject().put("\$set",message.body().getJsonObject("update"))
    mongo.updateCollection(collection.name, query, update) { result ->
      if (result.succeeded()) message.reply(result.result().docUpsertedId)
      else message.fail(1, result.cause().message)
    }
  }
  /**
   * Updates multiple records from collection matching the condition
   * @param message
   */
  private fun updateMultipleByIds(message: Message<JsonObject>){
    logger.info(enter())

    val query = JsonObject().put("_id", JsonObject().put("\$in",message.body().getJsonArray("ids")))
    val update = JsonObject().put("\$set",message.body().getJsonObject("update"))
    val options = UpdateOptions().setMulti(true)
    mongo.updateCollectionWithOptions(collection.name, query, update, options) { result ->
      if (result.succeeded()) message.reply(result.result().docUpsertedId)
      else message.fail(1, result.cause().message)
    }
  }
  /**
   * Counts the number of record from collection matching the condition
   * @param message
   */
  private fun count(message: Message<JsonObject>) {
    logger.info(enter())
    val query = if (message.body().containsKey("query"))  message.body().getJsonObject("query") else JsonObject()
    mongo.count(collection.name, query) { res ->
      if (res.succeeded())  message.reply(res.result())
      else message.fail(1, res.cause().message)
    }
  }
}

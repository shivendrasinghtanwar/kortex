package io.kortex.core.middlewares

import io.kortex.core.enums.CollectionNames
import io.kortex.core.enums.DataBusMessages
import io.kortex.core.enums.ErrorMessages
import io.kortex.core.locale
import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.kortex.core.utils.LoggerUtils.Companion.errorLog
import io.kortex.core.vertx
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger


/**
 * The reason why I use middleware is that vertx has this cool feature of eventBus by which it attains async behaviour
 * throughout the whole application but the problem that I face with it is that the syntax required for using this eventBus
 * specially if you are only making a CRUD from it, is to representational and this can cause un-intentional errors in a simple
 * code base so to make it more abstract I created this middleware layer where I can define the methods that are more relatable
 * to a programmer working on CRUD API app.
 */
open class DataMiddleware {
  open lateinit var collection : CollectionNames
  open lateinit var logger : Logger

  /**
   * @param query JsonObject
   */
  fun findAll(query: JsonObject = JsonObject()): Promise<JsonArray> {
    logger.info(enter())
    val promise = Promise.promise<JsonArray>()
    println(query.encodePrettily())
    vertx.eventBus().request<JsonArray>(DataBusMessages.Find.from(collection), JsonObject().put("query",query)){ find->
      if(find.succeeded()){
        promise.complete(find.result().body())
      }else
      {
        logger.error(errorLog(find.cause()))
        promise.fail(find.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject,
   * @param limit Int
   * @param skip Int
   */
  fun findLimited(query: JsonObject, limit: Int, skip: Int): Promise<JsonArray> {
    logger.info(enter())
    val promise = Promise.promise<JsonArray>()
    val data = JsonObject()
      .put("query",query)
      .put("limit",limit)
      .put("skip",skip)
    println(data.encodePrettily())
    vertx.eventBus().request<JsonArray>(DataBusMessages.Find.from(collection),data){ find->
      if(find.succeeded()){
        promise.complete(find.result().body())
      }else
      {
        logger.error(errorLog(find.cause()))
        promise.fail(find.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject
   */
  fun findById(userId: String): Promise<JsonObject> {
    logger.info(enter())
    val promise = Promise.promise<JsonObject>()
    vertx.eventBus().request<JsonObject>(DataBusMessages.FindById.from(collection),userId){ find->
      if(find.succeeded()){
        if(find.result().body()!=null) promise.complete(find.result().body())
        else promise.fail(ErrorMessages.NotFound.get(locale))
      }else
      {
        logger.error(errorLog(find.cause()))
        promise.fail(find.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject
   */
  fun insert(data: JsonObject): Promise<String> {
    logger.info(enter())
    val promise = Promise.promise<String>()
    vertx.eventBus().request<String>(DataBusMessages.Insert.into(collection), data){
      if(it.succeeded()) promise.complete(it.result().body())
      else{
        logger.error(errorLog(it.cause()))
        promise.fail(it.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject
   */
  fun deleteById(id: String): Promise<Long> {
    logger.info(enter())
    val promise = Promise.promise<Long>()
    vertx.eventBus().request<Long>(DataBusMessages.DeleteById.from(collection), id){
      if(it.succeeded()) promise.complete(it.result().body())
      else{
        logger.error(errorLog(it.cause()))
        promise.fail(it.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject
   */
  fun updateById(id: String,update: JsonObject): Promise<Long> {
    logger.info(enter())
    val promise = Promise.promise<Long>()
    val data = JsonObject()
      .put("id",id)
      .put("update",update)
    vertx.eventBus().request<Long>(DataBusMessages.UpdateById.from(collection), data){
      if(it.succeeded()) promise.complete(it.result().body())
      else{
        logger.error(errorLog(it.cause()))
        promise.fail(it.cause())
      }
    }
    return promise
  }
  /**
   * @param query JsonObject
   */
  fun count(query: JsonObject = JsonObject()): Promise<Long> {
    logger.info(enter())
    val promise = Promise.promise<Long>()
    val data = JsonObject().put("query",query)
    println(query.encodePrettily())
    vertx.eventBus().request<Long>(DataBusMessages.Count.from(collection), data){
      if(it.succeeded()) promise.complete(it.result().body())
      else{
        logger.error(errorLog(it.cause()))
        promise.fail(it.cause())
      }
    }
    return promise
  }
}

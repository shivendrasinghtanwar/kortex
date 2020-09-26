package io.kortex.core

import io.kortex.core.utils.LoggerUtils.Companion.errorLog
import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf
import io.vertx.kotlin.ext.auth.pubSecKeyOptionsOf
import java.util.*


var vertx: Vertx = Vertx.vertx()
val locale: Locale = Locale.ENGLISH
private const val publicKey = "f59a5e8974afa89cf98089077ef3cb31d7b53642c0e2ed3a759cd043a0ff007f" //secretStateTessinBillte in SHA256
val jwtAuthOptions = jwtAuthOptionsOf(
  pubSecKeys = listOf(
    pubSecKeyOptionsOf(
    algorithm = "HS256",
    publicKey = publicKey,
    symmetric = true)
  ))

var provider: JWTAuth = JWTAuth.create(vertx, jwtAuthOptions)
private lateinit var logger : Logger

fun main(args: Array<String>){
  //Initialise logger
  System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
  LoggerFactory.initialise()
  logger =  LoggerFactory.getLogger("App.kt")

  vertx.exceptionHandler { logger.error(errorLog(it)) }

  // Launch Server
  vertx.deployVerticle(Server())

}

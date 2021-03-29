package io.kortex.core

import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.kortex.core.utils.LoggerUtils.Companion.logException
import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf
import io.vertx.kotlin.ext.auth.pubSecKeyOptionsOf
import java.util.*
import kotlin.system.exitProcess


var vertx: Vertx = Vertx.vertx()
val locale: Locale = Locale.ENGLISH
private const val publicKey = "d48e9d2d0559a38f848dc6a0b534c545131b0c2d0b504a96d1187a1fb498f171" //vertxPlusKotlinIsKortex in SHA256
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

  vertx.exceptionHandler { logger.error(logException(it)) }

  //Retrieve Configuration for project
  val configFuture = Configuration.init().future()


  configFuture
    .onSuccess{ launchVerticles() }
    .onFailure{
      logger.error(logException(it))
      logger.error("!! Please check your configuration file at -> ${Configuration.configFilePath} !!")
      exitProcess(1)
    }

}

private fun launchVerticles() {
  logger.info(enter())
  /* Launch verticles here*/

  // Data Services


  // Services


  // Launch Server
  vertx.deployVerticle(Server())

}

package io.kortex.core

import io.kortex.core.utils.LoggerUtils.Companion.success
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.logging.LoggerFactory


class Server : AbstractVerticle() {
  private val serverPort : Int = 8888
  private val logger = LoggerFactory.getLogger(this::class.java)
  override fun start(startPromise: Promise<Void>) {
    vertx
      .createHttpServer()
      .requestHandler { req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!")
      }
      .listen(serverPort) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          logger.info("[SERVER STARTED] HTTP server started on port - $serverPort")
        }
        else startPromise.fail(http.cause())
      }
  }
}

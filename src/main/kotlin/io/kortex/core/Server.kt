package io.kortex.core

import io.kortex.core.controllers.getAPI
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*


class Server : AbstractVerticle() {

  private val serverPort : Int = 8888 // This can also be controlled by retrieving it from config file
  private val logger = LoggerFactory.getLogger(this::class.java)
  override fun start(startPromise: Promise<Void>) {

    // Setup router
    val router = Router.router(vertx)

    // Global router setup options
    router.route("/*").handler(ResponseTimeHandler.create())
    router.route("/*").handler(LoggerHandler.create())
    router.route(HttpMethod.POST,"/*").handler(BodyHandler.create())
    router.route(HttpMethod.PUT,"/*").handler(BodyHandler.create())
    // CORS configuration
    router.route().handler(CorsHandler.create("*")
      .allowedMethods(
        setOf(
          HttpMethod.POST,
          HttpMethod.GET,
          HttpMethod.PUT,
          HttpMethod.OPTIONS,
          HttpMethod.DELETE
        )
      )
      .allowedHeaders(
        setOf(
          "Authorization",
          "Content-Type",
          "Access-Control-Allow-Headers",
          "Access-Control-Allow-Origin",
          "Access-Control-Request-Method"
        )
      )
    )
    //Static handler
    val staticHandler = StaticHandler.create()
    staticHandler.setAllowRootFileSystemAccess(true)
    staticHandler.setWebRoot(Configuration.fileUploadsDir)
    router.route("/static/*").handler(staticHandler)
    val options = HttpServerOptions().setLogActivity(true)

    /**
     *  -------------------------------- API Routes ---------------------------------
     */
    router.route(HttpMethod.GET,"/get-api").handler{getAPI(it)}

    /**
     *  -------------------------------- Starting Server ---------------------------------
     */
    vertx
      .createHttpServer(options)
      .requestHandler(router)
      .listen(serverPort) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          logger.info("[SERVER STARTED] HTTP server started on port - $serverPort")
        }
        else startPromise.fail(http.cause())
      }
  }
}

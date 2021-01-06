package io.kortex.core.controllers

import io.kortex.core.utils.Responder
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun getAPI(ctx:RoutingContext) = GlobalScope.launch {
  val responder = Responder(ctx)
  try{

  }catch (e:Exception){
    responder.error(e)
  }
}

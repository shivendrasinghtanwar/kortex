package io.kortex.core.controllers

import io.kortex.core.utils.Responder
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun getAPI(ctx:RoutingContext) = GlobalScope.launch {
  val responder = Responder(ctx)
  try{
    responder.success()
  }catch (e:Exception){
    responder.error(e)
  }
}

fun postAPI(ctx:RoutingContext) = GlobalScope.launch {
  val responder = Responder(ctx)
  try{
    responder.success()
  }catch (e:Exception){
    responder.error(e)
  }
}

package io.kortex.core.utils

import io.kortex.core.models.ApiResponse
import io.kortex.core.models.ValidationError
import io.kortex.core.utils.LoggerUtils.Companion.enter
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext

class Responder(var ctx: RoutingContext) {

  private var response = JsonObject()
  private val logger = LoggerFactory.getLogger(this::class.java)
  //Success Functions
  fun success(){
    logger.info(enter())
    ctx
      .response()
      .putHeader("content-type", "application/json")
      .setStatusCode(200)
      .end(response.encode())
  }
  fun success(response: ApiResponse){
    try{
      setSuccessResponse(JsonObject.mapFrom(response))
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(data:JsonObject){
    try{
      this.response = JsonObject().put("data",data)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(data:JsonArray){
    try{
      this.response = JsonObject().put("data",data)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(data:List<Any>){
    try{
      this.response = JsonObject().put("data",data)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(response:String){
    try{
      setSuccessResponse(response)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(response:Double){
    try{
      setSuccessResponse(response)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  fun success(response:Long){
    try{
      setSuccessResponse(response)
      success()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }
  //Default Response
  fun defaultOkay() {
    setDefaultResponse()
    success()
  }
  //Error functions
  private fun error(errorCode: Int){
    ctx
      .response()
      .putHeader("content-type", "application/json")
      .setStatusCode(errorCode)
      .end(response.encode())
  }
  fun custom(errorList: ArrayList<String>,errorCode: Int){
    setErrorResponse(JsonArray(errorList))
    error(errorCode)
  }
  fun error(throwable: Throwable){
    throwable.printStackTrace()
    when(throwable) {
      is IllegalStateException -> errorBodyNotJson(throwable.message)
      else -> {
        setErrorResponse(throwable.message.toString())
        somethingWentWrong()
      }
    }
  }
  fun error(errorMessage: String?){
    setErrorResponse(errorMessage.toString())
    error(403)
  }
  fun validationFailed(validationErrors: ArrayList<ValidationError>){
    setValidationErrorResponse(validationErrors)
    badRequest()
  }
  private fun somethingWentWrong(){
    if(!response.containsKey("error")) setErrorResponse("Something went wrong!")
    error(500)
  }
  fun internalServerError(){
    setErrorResponse("Internal Server Error!")
    error(500)
  }
  fun forbidden(){
    setErrorResponse("Forbidden!")
    error(403)
  }
  fun notFound(data: String?){
    setErrorResponse("Not Found : $data!")
    error(404)
  }
  private fun errorBodyNotJson(message: String?){
    setErrorResponse("Error : $message")
    badRequest()
  }
  private fun badRequest(){
    error(400)
  }
  fun keyCannotBeNull(keyName:String){
    setErrorResponse("$keyName cannot be null!")
    badRequest()
  }
  fun keyCannotBeEmpty(keyName:String){
    setErrorResponse("$keyName cannot be empty!")
    badRequest()
  }
  fun keyIsMissing(keyName: String){
    setErrorResponse("$keyName - is missing!")
    badRequest()
  }
  //Set Response Functions
  private fun setDefaultResponse(){
    response = JsonObject().put("data", "OK")
  }
  private fun setSuccessResponse(data: Any){
    response = JsonObject().put("data", data)
  }
  private fun setErrorResponse(data: Any){
    response = JsonObject().put("error", data)
  }
  private fun setValidationErrorResponse(validationErrors: ArrayList<ValidationError>){
    val errors = JsonArray()
    validationErrors.forEach { errors.add(JsonObject().put(it.key,it.value)) }
    response = JsonObject().put("error", errors)
  }
}

package io.kortex.core.utils

import io.vertx.core.json.JsonObject

class LoggerUtils {
  companion object{
    fun errorLog(errorMessage: String?):String{
      println(Thread.currentThread().stackTrace)
      return "[ERROR] - $errorMessage"
    }
    fun errorLog(errorMessage: Throwable):String{
      errorMessage.printStackTrace()
      return "[ERROR] - ${errorMessage.message}"
    }
    fun enter():String{
      return "[ENTER] - ${Thread.currentThread().stackTrace[2].methodName}"
    }
    fun success(extra: JsonObject = JsonObject()):String{
      return "[SUCCESS] - Function -> ${Thread.currentThread().stackTrace[2].methodName} | Data -> ${extra.encodePrettily()}"
    }
    fun success(workName:String= "",extra: Any):String{
      return "[SUCCESS] - Function -> ${Thread.currentThread().stackTrace[2].methodName} | Work -> $workName | Data -> $extra"
    }
    fun success():String{
      return "[SUCCESS] - Function -> ${Thread.currentThread().stackTrace[2].methodName} | Class -> ${Thread.currentThread().stackTrace[2].className}"
    }
    fun success(workName:String= ""):String{
      return "[SUCCESS] - $workName"
    }
    fun deployed(classPathName:String):String{
      return "[DEPLOYED] - ${classPathName.split(".").last()}"
    }
  }
}

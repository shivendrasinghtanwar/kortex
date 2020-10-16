package io.kortex.core.models


import kotlin.reflect.KType

data class ValidationResult(var pass: Boolean, var message: ArrayList<ValidationError>)
data class ValidationError(var key:String?, var value:Any?)
data class ClassMember(var name: String, var type: KType)

open class LoginResponse(var token: String) :ApiResponse {
  var type: String?= "Bearer"
  var expiresIn: Int?= 3600
}

data class DefaultResponse(var key: String,var value: Any)

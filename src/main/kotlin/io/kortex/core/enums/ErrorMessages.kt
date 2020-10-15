package io.kortex.core.enums

import java.util.*

enum class ErrorMessages {
  AlreadyExists,
  NotFound,
  MailAlreadyVerified,
  MailNotVerified,
  IncorrectVerificationCode,
  UsernameOrPasswordIncorrect,
  PasswordAndConfirmPasswordDoNotMatch,
  FirstLoginAlreadyDone,
  OldPasswordAndNewPasswordCannotBeSame,
  Unauthorized,
  PasswordNotSet;

  fun get(locale: Locale):String{
    return ResourceBundle
      .getBundle("messages", locale)
      .getString(this.name)
  }
}

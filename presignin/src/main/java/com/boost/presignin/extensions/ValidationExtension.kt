package com.boost.presignin.extensions

import android.util.Patterns
import com.framework.utils.ValidationUtils.isMobileNumberValid
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String?.isNameValid(): Boolean {
  if (this == null) return false
  return this.trim().length > 2
}

fun String?.isBusinessNameValid(): Boolean {
  if (this == null) return false
  return this.trim().length > 2
}

fun String?.validateLetters(): Boolean {
  if (this.toString().isEmpty()||this.toString().isBlank()){
    return false
  }
  val regx = "^[a-zA-Z0-9 ]*\$"
  val matcher: Matcher = Pattern.compile(regx, Pattern.CASE_INSENSITIVE).matcher(this.toString())
  return matcher.find()
}

fun String?.isEmailValid(): Boolean {
  if (this == null) return false
  return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String?.isPhoneValid(): Boolean {
  return (this.isNullOrEmpty().not() && isMobileNumberValid(this!!))
}

fun String?.isWebsiteValid(): Boolean {
  if (this == null) return false
  return this.trim().isNotEmpty()
}
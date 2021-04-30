package com.boost.presignin.extensions

import android.util.Patterns
import com.framework.utils.ValidationUtils.isMobileNumberValid

fun String?.isNameValid(): Boolean {
  if (this == null) return false
  return this.trim().length > 2
}

fun String?.isBusinessNameValid(): Boolean {
  if (this == null) return false
  return this.trim().length > 2
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
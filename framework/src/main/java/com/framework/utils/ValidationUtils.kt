package com.framework.utils

import java.util.regex.Pattern

object ValidationUtils {

  fun isMobileNumberValid(mobile: String): Boolean {
    return Pattern.matches("[6-9][0-9]{9}", mobile)
  }

  fun isEmailValid(email: String): Boolean {
    return Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)
  }
}

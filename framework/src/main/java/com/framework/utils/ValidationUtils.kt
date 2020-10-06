package com.framework.utils

import java.util.regex.Pattern.matches

object ValidationUtils {

  fun isMobileNumberValid(mobile: String): Boolean {
    return matches("[6-9][0-9]{9}", mobile)
  }

  fun isEmailValid(email: String): Boolean {
    return matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)
  }

  fun isNumeric(str: String): Boolean {
    return matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$", str)
  }
}

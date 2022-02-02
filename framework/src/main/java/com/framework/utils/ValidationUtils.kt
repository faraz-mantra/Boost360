package com.framework.utils

import java.util.regex.Pattern
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


  fun isValidName(name: String): Boolean {
    val regExp = "^[^<>{}\"/|;:.,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©0-9_+]*$"
    return Pattern.compile(regExp, Pattern.CASE_INSENSITIVE).matcher(name).matches()
  }

  fun isBankAcValid(str: String): Boolean {
    if (str.length in 19..8) {
      return false
    }
    val n = str.length
    for (i in 1 until n) if (str[i] != str[0]) return false
    return true
  }


  fun isDomainValid(domainString: CharSequence?): Boolean {
    var domainWithoutWWW = ""
    if (domainString.isNullOrEmpty() || domainString.isNullOrBlank())
      return false

    domainWithoutWWW = if (domainString.startsWith("www"))
      domainString.toString().replace("www.", "")
    else
      domainString.toString()
    val domainSelection = arrayListOf("com", "net", "in", "ca", "org")
    if (Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}").matcher(domainWithoutWWW).matches()) {
      val substringAfterLast = domainWithoutWWW.substringAfterLast(".").lowercase()
      return domainSelection.contains(substringAfterLast) || substringAfterLast.contains("co.za") || substringAfterLast.contains("co.in")
    }

    return false
  }
}

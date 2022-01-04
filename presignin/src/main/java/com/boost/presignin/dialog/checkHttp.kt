package com.boost.presignin.dialog

import com.onboarding.nowfloats.utils.checkHttp

fun String.checkHttp(): String {
  return when {
    (this.startsWith("http") || this.startsWith("https")).not() -> "https://$this"
    this.startsWith("http") -> this.replace("http", "https")
    else -> this
  }
}
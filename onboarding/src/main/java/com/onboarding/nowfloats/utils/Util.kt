package com.onboarding.nowfloats.utils

import com.onboarding.nowfloats.extensions.checkIsFile

fun String.getWebViewUrl(): String {
  return (takeIf { checkIsFile().not() }?.let { this } ?: "https://docs.google.com/viewer?url=$this").checkHttp()
}

fun String.checkHttp(): String {
  return if ((this.startsWith("http://") || this.startsWith("https://")).not()) "http://$this" else this
}
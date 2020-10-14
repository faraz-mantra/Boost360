package com.onboarding.nowfloats.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.onboarding.nowfloats.extensions.checkIsFile

fun String.getWebViewUrl(): String {
  return (takeIf { checkIsFile().not() }?.let { this } ?: "https://docs.google.com/viewer?url=$this").checkHttp()
}

fun String.checkHttp(): String {
  return if ((this.startsWith("http://") || this.startsWith("https://")).not()) "http://$this" else this
}

fun Context.openWebPage(url: String): Boolean {
  return try {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    true
  } catch (e: Exception) {
    false
  }
}
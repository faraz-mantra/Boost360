package com.onboarding.nowfloats.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import com.onboarding.nowfloats.extensions.checkIsFile

fun String.getWebViewUrl(): String {
  return (takeIf { checkIsFile().not() }?.let { this } ?: "https://docs.google.com/viewer?url=$this").checkHttp()
}

fun String.checkHttp(): String {
  return when {
    (this.startsWith("http") || this.startsWith("https")).not() -> "https://$this"
    this.startsWith("http") -> this.replace("http", "https")
    else -> this
  }
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


fun viewToBitmap(view: View): Bitmap? {
  val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  view.draw(canvas)
  return bitmap
}
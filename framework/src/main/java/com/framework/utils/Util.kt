package com.framework.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.NumberFormat
import java.util.*

fun Activity.hideKeyBoard() {
  val view = this.currentFocus
  if (view != null) {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }
}

fun Activity.showKeyBoard(view: View?) {
  view?.post {
    view.requestFocus()
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
  }
}

fun fromHtml(html: String?): Spanned? {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
  else Html.fromHtml(html)
}

fun getNumberFormat(value: String): String {
  return try {
    NumberFormat.getNumberInstance(Locale.US).format(value.toInt().toLong())
  } catch (e: Exception) {
    value
  }
}


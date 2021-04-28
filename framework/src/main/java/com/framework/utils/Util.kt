package com.framework.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
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

fun Double.roundTo(n: Int) : Double {
  return "%.${n}f".format(this).toDouble()
}


fun AppCompatActivity.getStatusBarHeight(): Int {
  var result = 0
  val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}

fun AppCompatActivity.getNavigationBarHeight(): Int {
  val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
  val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
  return if (resourceId > 0 && !hasMenuKey) {
    resources.getDimensionPixelSize(resourceId)
  } else 0
}

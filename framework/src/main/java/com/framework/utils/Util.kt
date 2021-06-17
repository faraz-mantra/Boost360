package com.framework.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.framework.views.customViews.CustomTextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
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

fun CustomTextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
  val spannableString = SpannableString(this.text)
  var startIndexOfLink = -1
  for (link in links) {
    val clickableSpan = object : ClickableSpan() {
      override fun updateDrawState(textPaint: TextPaint) {
        // use this to change the link color
        textPaint.color = textPaint.linkColor
        // toggle below value to enable/disable
        // the underline shown below the clickable text
        textPaint.isUnderlineText = true
      }

      override fun onClick(view: View) {
        Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
        view.invalidate()
        link.second.onClick(view)
      }
    }
    startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
    spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
  this.setText(spannableString, TextView.BufferType.SPANNABLE)
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
val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024
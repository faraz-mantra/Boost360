package com.framework.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.framework.views.customViews.CustomTextView
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun View.setNoDoubleClickListener(listener: View.OnClickListener, blockInMillis: Long = 1000) {
  var lastClickTime: Long = 0
  this.setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClickTime < blockInMillis) return@setOnClickListener
    lastClickTime = SystemClock.elapsedRealtime()
    listener.onClick(this)
  }
}

fun Double.roundToFloat(numFractionDigits: Int): Float = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toFloat()

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

fun hasHTMLTags(text: String): Boolean {
  val matcher: Matcher = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>").matcher(text)
  return matcher.find()
}

fun fromHtml(html: String?): Spanned? {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
    html,
    Html.FROM_HTML_MODE_LEGACY
  )
  else Html.fromHtml(html)
}

fun getNumberFormat(value: String): String {
  return try {
    NumberFormat.getNumberInstance(Locale.US).format(value.toInt().toLong())
  } catch (e: Exception) {
    value
  }
}

fun Double.roundTo(n: Int): Double {
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
    spannableString.setSpan(
      clickableSpan,
      startIndexOfLink,
      startIndexOfLink + link.first.length,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
  }
  this.movementMethod =
    LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
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

fun LottieAnimationView.changeLayersColor(
  @ColorRes colorRes: Int
) {
  val color = ContextCompat.getColor(context, colorRes)
  val filter = SimpleColorFilter(color)
  val keyPath = KeyPath("**")
  val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
  addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}

inline fun <reified T> read(): T {
  val value: String = readLine()!!
  return when (T::class) {
    Int::class -> value.toInt() as T
    String::class -> value as T
    // add other types here if need
    else -> throw IllegalStateException("Unknown Generic Type")
  }
}

fun Activity.makeCall(number: String) {
  val callIntent = Intent(Intent.ACTION_DIAL)
  callIntent.addCategory(Intent.CATEGORY_DEFAULT)
  callIntent.data = Uri.parse("tel:$number")
  this.startActivity(Intent.createChooser(callIntent, "Call by:"))
}
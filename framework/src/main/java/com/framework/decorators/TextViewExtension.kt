package com.framework.decorators

import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView

fun AppCompatTextView.setTextColorCompat(@ColorInt color: Int) {
  this.setTextColor(color)
}

fun AppCompatTextView.underlineText(start: Int, end: Int) {
  val spannableString = SpannableString(text)
  spannableString.setSpan(UnderlineSpan(), start, end + 1, 0)
  this.text = spannableString
}
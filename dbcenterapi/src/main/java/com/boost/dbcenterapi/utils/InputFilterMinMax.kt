package com.boost.dbcenterapi.utils

import android.text.InputFilter
import android.text.Spanned
import com.framework.analytics.SentryController


class InputFilterRange(private var range: IntRange) : InputFilter {

  override fun filter(
    source: CharSequence,
    start: Int,
    end: Int,
    dest: Spanned,
    dstart: Int,
    dend: Int
  ) = try {
    val input = Integer.parseInt(dest.toString() + source.toString())
    if (range.contains(input)) null else ""
  } catch (nfe: NumberFormatException) {
    SentryController.captureException(nfe)
    ""
  }
}
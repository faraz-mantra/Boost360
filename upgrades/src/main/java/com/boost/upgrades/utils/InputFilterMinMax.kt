package com.boost.upgrades.utils

import android.text.Spanned

import android.text.InputFilter
import java.lang.NumberFormatException


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
    ""
  }
}
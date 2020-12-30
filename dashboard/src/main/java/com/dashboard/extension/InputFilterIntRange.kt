package com.dashboard.extension

import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.EditText


class InputFilterIntRange(min: Int, max: Int) : InputFilter, View.OnFocusChangeListener {
  private val min: Int
  private val max: Int
  override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {

    // Determine the final string that will result from the attempted input
    val destString = dest.toString()
    val inputString = destString.substring(0, dstart) + source.toString() + destString.substring(dstart)

    // Don't prevent - sign from being entered first if min is negative
    if (inputString.equals("-", ignoreCase = true) && min < 0) return null
    try {
      val input = inputString.toInt()
      if (mightBeInRange(input)) return null
    } catch (nfe: NumberFormatException) {
    }
    return ""
  }

  override fun onFocusChange(v: View, hasFocus: Boolean) {

    // Since we can't actively filter all values
    // (ex: range 25 -> 350, input "15" - could be working on typing "150"),
    // lock values to range after text loses focus
    if (!hasFocus) {
      if (v is EditText) sanitizeValues(v)
    }
  }

  private fun mightBeInRange(value: Int): Boolean {
    // Quick "fail"
    if (value >= 0 && value > max) return false
    if (value >= 0 && value >= min) return true
    if (value < 0 && value < min) return false
    if (value < 0 && value <= max) return true
    val negativeInput = value < 0

    // If min and max have the same number of digits, we can actively filter
    if (numberOfDigits(min) == numberOfDigits(max)) {
      if (!negativeInput) {
        if (numberOfDigits(value) >= numberOfDigits(min) && value < min) return false
      } else {
        if (numberOfDigits(value) >= numberOfDigits(max) && value > max) return false
      }
    }
    return true
  }

  private fun numberOfDigits(n: Int): Int {
    return n.toString().replace("-", "").length
  }

  private fun sanitizeValues(valueText: EditText) {
    try {
      var value = valueText.text.toString().toInt()
      // If value is outside the range, bring it up/down to the endpoint
      if (value < min) {
        value = min
        valueText.setText(value.toString())
      } else if (value > max) {
        value = max
        valueText.setText(value.toString())
      }
    } catch (nfe: NumberFormatException) {
      valueText.setText("")
    }
  }

  init {
    var min = min
    var max = max
    if (min > max) {
      // Input sanitation for the filter itself
      val mid = max
      max = min
      min = mid
    }
    this.min = min
    this.max = max
  }
}
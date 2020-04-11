package com.framework.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

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
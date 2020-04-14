package com.framework.extensions

import android.view.View
import android.view.ViewGroup

fun ViewGroup.getChildOrNull(index: Int): View? {
  return if (index < this.childCount) {
    this.getChildAt(index)
  } else {
    null
  }
}
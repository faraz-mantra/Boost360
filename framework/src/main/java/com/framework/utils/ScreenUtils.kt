package com.framework.utils

import android.app.Activity
import android.util.DisplayMetrics


class ScreenUtils {

  companion object {
    val instance = ScreenUtils()
  }

  fun getDisplayMetrics(activity: Activity?): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics
  }

  fun getWidth(activity: Activity?): Int {
    return getDisplayMetrics(activity).widthPixels
  }

  fun getHeight(activity: Activity?): Int {
    return getDisplayMetrics(activity).heightPixels
  }
}
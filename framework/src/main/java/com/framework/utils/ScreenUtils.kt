package com.framework.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.Window
import androidx.annotation.NonNull


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

  fun setWhiteNavigationBar(@NonNull dialog: Dialog) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        try {
          val window: Window? = dialog.window
          if (window != null) {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            // ...customize your dim effect here
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)
            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }
}
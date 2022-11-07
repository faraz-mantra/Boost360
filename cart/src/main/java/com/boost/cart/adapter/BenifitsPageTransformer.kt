package com.boost.cart.adapter

import android.app.Activity
import android.graphics.Insets
import android.view.View
import androidx.core.view.marginLeft
import androidx.viewpager2.widget.ViewPager2
import com.boost.cart.R
import android.util.DisplayMetrics
import android.view.WindowInsets

import android.view.WindowMetrics

import android.os.Build
import androidx.annotation.NonNull


class BenifitsPageTransformer(val activity: Activity) : ViewPager2.PageTransformer {

  override fun transformPage(view: View, position: Float) {
    view.apply {
      when {
        position < -1 -> { // [-Infinity,-1)
          //This page is way off-screen to the left.
          alpha = 1f
        }
        position <= 1 -> { // [-1,1]

//          val displayMetrics = DisplayMetrics()
//          activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
//          val width = displayMetrics.widthPixels
          val width = getScreenWidth(activity)
//          val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_benifits)
          val nextItemVisiblePx = width * 0.3f
          val currentItemHorizontalMarginPx = width * 0.06f
//        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_horizontal_margin_benifits)
          val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx

//          val scaleFactor = 1 - (0.50f * Math.abs(position))
          translationX = -pageTranslationX * position
//          left = 0
//                    scaleY = scaleFactor

          alpha = 1f
        }
        else -> { // (1,+Infinity]
          // This page is way off-screen to the right.
          alpha = 1f
        }
      }
    }
  }

  fun getScreenWidth(@NonNull activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowMetrics = activity.windowManager.currentWindowMetrics
      val insets: Insets = windowMetrics.windowInsets
        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
      windowMetrics.bounds.width() - insets.left - insets.right
    } else {
      val displayMetrics = DisplayMetrics()
      activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
      displayMetrics.widthPixels
    }
  }
}
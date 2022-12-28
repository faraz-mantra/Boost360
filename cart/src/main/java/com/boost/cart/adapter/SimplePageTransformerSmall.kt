package com.boost.cart.adapter

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.boost.cart.R

class SimplePageTransformerSmall : ViewPager2.PageTransformer {

  override fun transformPage(view: View, position: Float) {
    view.apply {
      when {
        position < -1 -> { // [-Infinity,-1)
          // This page is way off-screen to the left.
          alpha = 1f
        }
        position <= 1 -> { // [-1,1]

          val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible2)
          val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin5)
          val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx

//          val scaleFactor = 1 - (0.50f * Math.abs(position))
          translationX = -pageTranslationX * position
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
}
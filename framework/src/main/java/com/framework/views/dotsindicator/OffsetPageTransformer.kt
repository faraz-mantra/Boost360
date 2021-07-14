package com.framework.views.dotsindicator

import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.framework.R


class OffsetPageTransformer : ViewPager2.PageTransformer {

  override fun transformPage(page: View, position: Float) {
    val viewPager = page.parent.parent as ViewPager2
    val pageMarginPx =
      viewPager.context?.resources?.getDimensionPixelOffset(R.dimen._6dp) ?: PAGE_MARGIN
    val offsetPx =
      viewPager.context?.resources?.getDimensionPixelOffset(R.dimen._14dp) ?: PAGE_OFFSET
    val offset = position * -(2 * offsetPx + pageMarginPx)
    if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
      if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
        page.translationX = -offset
      } else {
        page.translationX = offset
      }
    } else {
      page.translationY = offset
    }
  }

  companion object {
    private const val PAGE_OFFSET = 14
    private const val PAGE_MARGIN = 6
  }

}

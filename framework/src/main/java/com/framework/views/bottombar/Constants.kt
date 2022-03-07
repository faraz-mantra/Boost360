package com.framework.views.bottombar

import androidx.core.content.ContextCompat
import com.framework.BaseApplication
import com.framework.R

/**
 * This is a static class to hold constants
 */
object Constants {
  const val ITEM_TAG = "item"
  const val ICON_ATTRIBUTE = "icon"
  const val TITLE_ATTRIBUTE = "title"
  const val WHITE_COLOR_HEX = "#FFFFFF"

  val DEFAULT_INDICATOR_COLOR = "#" + Integer.toHexString(ContextCompat.getColor(BaseApplication.instance, R.color.colorAccent))
  const val DEFAULT_TEXT_COLOR = "#9B9B9B"
  val DEFAULT_TEXT_COLOR_ACTIVE = "#" + Integer.toHexString(ContextCompat.getColor(BaseApplication.instance, R.color.colorAccent))
}

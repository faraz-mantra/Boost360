package com.framework.views.bottombar

import android.graphics.RectF
import android.graphics.drawable.Drawable

data class BottomBarItem(
  var title: String, var icon: Drawable,
  var rect: RectF = RectF(), var badgeSize: Float = 0f,
  var badgeText: String? = null

)
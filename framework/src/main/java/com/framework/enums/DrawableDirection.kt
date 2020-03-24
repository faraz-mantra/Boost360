package com.framework.enums

import android.graphics.drawable.Drawable
import android.widget.TextView
import com.framework.enums.DrawableDirection.*

enum class DrawableDirection {
  LEFT, TOP, RIGHT, BOTTOM
}

fun TextView.setDrawable(drawable: Drawable?, direction: DrawableDirection) {
  val drawables = compoundDrawables
  var left = drawables[0]
  var top = drawables[1]
  var right = drawables[2]
  var bottom = drawables[3]

  when (direction) {
    LEFT -> left = drawable
    TOP -> top = drawable
    RIGHT -> right = drawable
    BOTTOM -> bottom = drawable
  }

  setCompoundDrawables(left, top, right, bottom)
}
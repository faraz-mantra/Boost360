package com.framework.decorators

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes


fun Resources.getDrawable(context: Context, @DrawableRes res: Int): Drawable {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    context.resources.getDrawable(res, context.theme)
  } else {
    context.resources.getDrawable(res)
  }
}
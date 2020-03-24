package com.framework.decorators

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.framework.utils.ConversionUtils
import com.framework.views.CustomDividerItemDecoration
import com.framework.views.VerticalSpaceItemDecoration

fun RecyclerView.addDivider(context: Context, spacing: Float = 8f, @DrawableRes layout: Int) {
  this.addItemDecoration(CustomDividerItemDecoration(context, layout))
  this.addItemDecoration(VerticalSpaceItemDecoration(ConversionUtils.dp2px(spacing)))
}

fun RecyclerView.removeItemDecorations() {
  while (this.itemDecorationCount > 0) {
    this.removeItemDecorationAt(0)
  }
  this.invalidateItemDecorations()
}
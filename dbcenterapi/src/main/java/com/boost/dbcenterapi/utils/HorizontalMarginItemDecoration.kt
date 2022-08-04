package com.boost.dbcenterapi.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Adds margin to the left and right sides of the RecyclerView item.
 * Adapted from https://stackoverflow.com/a/27664023/4034572
 * @param horizontalMarginInDp the margin resource, in dp.
 */

class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginLeftInDp: Int, @DimenRes horizontalMarginRightInDp: Int) :
  RecyclerView.ItemDecoration() {

  private val horizontalMarginLeftInDp: Int =
    context.resources.getDimension(horizontalMarginLeftInDp).toInt()
  private val horizontalMarginRightInDp: Int =
    context.resources.getDimension(horizontalMarginRightInDp).toInt()

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
  ) {
    outRect.right = horizontalMarginRightInDp
    outRect.left = horizontalMarginLeftInDp
  }

}
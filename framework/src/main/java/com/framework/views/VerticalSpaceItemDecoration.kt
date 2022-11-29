package com.framework.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @param isLastPositionBottomSpaceDifferentNeeded : If you want different space for last index then keep this true.
 * @param bottomSpace : Special different space is used in case 'isLastPositionBottomSpaceDifferentNeeded' is true
 * */

class VerticalSpaceItemDecoration(private val generalVerticalSpaceHeight: Int, private val isLastPositionBottomSpaceDifferentNeeded: Boolean = false, private val bottomSpace: Int = 0) :
  RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
      outRect.bottom = generalVerticalSpaceHeight
    }else if (isLastPositionBottomSpaceDifferentNeeded && parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)){
      outRect.bottom = bottomSpace
    }
  }
}
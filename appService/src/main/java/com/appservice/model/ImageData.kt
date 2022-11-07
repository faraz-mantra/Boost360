package com.appservice.model

import com.appservice.recyclerView.AppBaseRecyclerViewItem

data class ImageData(
  var url: String? = null,
  var layout: Int
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return layout
  }
}

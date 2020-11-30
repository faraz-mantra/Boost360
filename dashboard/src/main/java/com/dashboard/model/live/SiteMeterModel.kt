package com.dashboard.model.live

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class SiteMeterModel(
    var position: Int? = null,
    var Title: String? = null,
    var Desc: String? = null,
    var Percentage: String? = null,
    var status: Boolean? = null,
    var sortChar: Int = 0,
    var isPost: Boolean = false,
) : AppBaseRecyclerViewItem, Serializable, Comparable<Any?> {

  var recyclerViewItemType: Int = RecyclerViewItemType.ITEMS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  override fun compareTo(other: Any?): Int {
    val cVal = (other as? SiteMeterModel)?.sortChar ?: 0
    return cVal - sortChar
  }

  fun getIcon(): Int {
    return if (status == true) R.drawable.ic_ok_11_d else R.drawable.ic_circle_grey_9_d
  }
}
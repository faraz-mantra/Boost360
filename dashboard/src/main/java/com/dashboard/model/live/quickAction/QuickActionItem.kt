package com.dashboard.model.live.quickAction

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

class QuickActionItem(
    var title: String? = null,
    var quickActionType: String = "",
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.QUICK_ACTION_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }
}
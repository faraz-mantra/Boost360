package com.dashboard.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class BoostPremiumData(
  var title: String? = null,
  var subTitle: String? = null,
  var icon1: Int? = null,
  var icon2: Int? = null,
  var recyclerViewItemType: Int = RecyclerViewItemType.BOOST_PREMIUM_ITEM_VIEW.getLayout()
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

}
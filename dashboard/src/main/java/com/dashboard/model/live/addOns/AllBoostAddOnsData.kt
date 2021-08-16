package com.dashboard.model.live.addOns

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class AllBoostAddOnsData(
  var title: String? = null,
  var subTitle: String? = null,
  var manageBusinessList: ArrayList<ManageBusinessData>? = null,
  var isLastSeen: Boolean = false,
  var isExpend: Boolean = true,
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ALL_BOOST_ADD_ONS_VIEW.getLayout()
  }
}


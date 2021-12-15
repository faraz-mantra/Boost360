package com.boost.marketplace.infra.api.models.live.addOns

import com.boost.marketplace.infra.constant.RecyclerViewItemType
import com.boost.marketplace.recyclerView.AppBaseRecyclerViewItem
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


package com.onboarding.nowfloats.model.feature

import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

@Deprecated("old")
class FeatureDetailsModel(
  val description: String? = null,
  val title: String? = null
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.FEATURE_DETAILS_BOTTOM_SHEET_ITEM.getLayout()
  }
}
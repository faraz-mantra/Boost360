package com.onboarding.nowfloats.model

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.util.*

class CityDataModel(
    val id: Int? = null,
    val name: String = "",
    val state: String = ""
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.CITY_DETAILS_ITEM.getLayout()
  }

  fun getCityName(): String {
    return name.toLowerCase(Locale.ROOT)
  }

  fun getStateName(): String {
    return state.toLowerCase(Locale.ROOT)
  }

}

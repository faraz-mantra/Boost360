package com.onboarding.nowfloats.model.digitalCard

import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class DigitalCardData(
    var title: String? = null,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewType = RecyclerViewItemType.DIGITAL_CARD_ITEM.getLayout()

  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun getData(): ArrayList<DigitalCardData> {
    val list = ArrayList<DigitalCardData>()
    list.add(DigitalCardData())
    list.add(DigitalCardData())
    return list
  }
}
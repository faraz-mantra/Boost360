package com.onboarding.nowfloats.model.digitalCard

import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class DigitalCardData(
    var businessName: String? = null,
    var businessLogo: String? = null,
    var location: String? = null,
    var name: String? = null,
    var number: String? = null,
    var email: String? = null,
    var businessType: String? = null,
    var website: String? = null,
    var color1: Int? = null,
    var color2: Int? = null,
    var textColor1: Int? = null,
    var textColor2: Int? = null,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewType = RecyclerViewItemType.DIGITAL_CARD_ITEM.getLayout()

  override fun getViewType(): Int {
    return recyclerViewType
  }
}
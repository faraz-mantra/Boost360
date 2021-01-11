package com.onboarding.nowfloats.model.digitalCard

import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class DigitalCardData(
    var cardData: CardData? = null,
    var recyclerViewType :Int = RecyclerViewItemType.VISITING_CARD_ONE_ITEM.getLayout()
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return recyclerViewType
  }
}
data class CardData(
    var businessName: String? = null,
    var businessLogo: String? = null,
    var location: String? = null,
    var name: String? = null,
    var number: String? = null,
    var email: String? = null,
    var businessType: String? = null,
    var website: String? = null
)
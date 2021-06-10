package com.onboarding.nowfloats.model.digitalCard

import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.saveData
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

const val LAST_SHARE_CARD = "LAST_SHARE_CARD"

data class DigitalCardData(
  var cardData: CardData? = null,
  var recyclerViewType: Int = RecyclerViewItemType.VISITING_CARD_ONE_ITEM.getLayout(),
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
  var website: String? = null,
  var cardIcon: Int? = null,
) : Serializable


fun savePositionCard(posCard: Int) {
  PreferencesUtils.instance.saveData(LAST_SHARE_CARD, posCard)
}

fun getLastShareCard(): Int {
  return PreferencesUtils.instance.getData(LAST_SHARE_CARD, 0)
}
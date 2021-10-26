package dev.patrickgold.florisboard.customization.model.response

import com.onboarding.nowfloats.model.digitalCard.CardData
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.io.Serializable

data class DigitalCardDataKeyboard (
  var cardData: CardData? = null,
  var recyclerViewType: Int = FeaturesEnum.VISITING_CARD_ONE_ITEM.ordinal
) : Serializable, BaseRecyclerItem(){

  override fun getViewType(): Int {
    return recyclerViewType
  }
}
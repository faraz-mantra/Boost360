package dev.patrickgold.florisboard.customization.adapter

import androidx.annotation.LayoutRes
import dev.patrickgold.florisboard.R

enum class FeaturesEnum {
  LOADER,
  PRODUCTS,
  UPDATES,
  PHOTOS,
  DETAILS,
  VISITING_CARD_ONE_ITEM,
  VISITING_CARD_TWO_ITEM,
  VISITING_CARD_THREE_ITEM,
  VISITING_CARD_FOUR_ITEM,
  VISITING_CARD_FIVE_ITEM,
  VISITING_CARD_SIX_ITEM,
  VISITING_CARD_SEVEN_ITEM,
  VISITING_CARD_EIGHT_ITEM,
  VISITING_CARD_NINE_ITEM,
  VISITING_CARD_TEN_ITEM;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      LOADER -> R.layout.pagination_loader_keyboard
      UPDATES -> R.layout.adapter_item_update
      PRODUCTS -> R.layout.adapter_item_product_new
      PHOTOS -> R.layout.adapter_item_photos
      DETAILS -> R.layout.adapter_item_details
      VISITING_CARD_ONE_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_one
      VISITING_CARD_TWO_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_two
      VISITING_CARD_THREE_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_three
      VISITING_CARD_FOUR_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_four
      VISITING_CARD_FIVE_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_five
      VISITING_CARD_SIX_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_six
      VISITING_CARD_SEVEN_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_seven
      VISITING_CARD_EIGHT_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_eight
      VISITING_CARD_NINE_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_nine
      VISITING_CARD_TEN_ITEM -> com.onboarding.nowfloats.R.layout.item_visiting_card_ten
    }
  }
}
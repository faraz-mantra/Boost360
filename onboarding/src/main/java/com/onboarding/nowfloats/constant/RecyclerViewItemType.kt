package com.onboarding.nowfloats.constant

import androidx.annotation.LayoutRes
import com.onboarding.nowfloats.R

enum class RecyclerViewItemType {
  CATEGORY_ITEM,
  CHANNEL_ITEM,
  SELECTED_CHANNEL_ITEM,
  SMALL_SELECTED_CHANNEL_ITEM,
  FEATURE_ITEM,
  FEATURE_DETAILS_BOTTOM_SHEET_ITEM,
  API_PROCESS_BUSINESS_ITEM,
  API_PROCESS_CHANNEL_ITEM,
  CHANNEL_BOTTOM_SHEET_ITEM,
  SECTION_HEADER_ITEM,
  CITY_DETAILS_ITEM,
  CHANNEL_ITEM_CONNECT,
  CHANNEL_ITEM_DISCONNECT,
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
      CATEGORY_ITEM -> R.layout.item_category
      CHANNEL_ITEM -> R.layout.item_channel
      SELECTED_CHANNEL_ITEM -> R.layout.item_selected_channel
      SMALL_SELECTED_CHANNEL_ITEM -> R.layout.item_selected_channel_small
      FEATURE_ITEM -> R.layout.item_channel_feature
      CHANNEL_BOTTOM_SHEET_ITEM -> R.layout.item_channel_bottom_sheet
      SECTION_HEADER_ITEM -> R.layout.item_section_header
      FEATURE_DETAILS_BOTTOM_SHEET_ITEM -> R.layout.item_feature_details_bottom_sheet
      API_PROCESS_BUSINESS_ITEM -> R.layout.item_api_calling_process
      API_PROCESS_CHANNEL_ITEM -> R.layout.item_child_api_calling
      CITY_DETAILS_ITEM -> R.layout.city_item_data
      CHANNEL_ITEM_CONNECT -> R.layout.item_channels_connected
      CHANNEL_ITEM_DISCONNECT -> R.layout.item_channels_disconnect
      VISITING_CARD_ONE_ITEM -> R.layout.item_visiting_card_one
      VISITING_CARD_TWO_ITEM-> R.layout.item_visiting_card_two
      VISITING_CARD_THREE_ITEM-> R.layout.item_visiting_card_three
      VISITING_CARD_FOUR_ITEM-> R.layout.item_visiting_card_four
      VISITING_CARD_FIVE_ITEM-> R.layout.item_visiting_card_five
      VISITING_CARD_SIX_ITEM-> R.layout.item_visiting_card_six
      VISITING_CARD_SEVEN_ITEM-> R.layout.item_visiting_card_seven
      VISITING_CARD_EIGHT_ITEM-> R.layout.item_visiting_card_eight
      VISITING_CARD_NINE_ITEM-> R.layout.item_visiting_card_nine
      VISITING_CARD_TEN_ITEM-> R.layout.item_visiting_card_ten
    }
  }
}

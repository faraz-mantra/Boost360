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
  SECTION_HEADER_ITEM;

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
    }
  }
}

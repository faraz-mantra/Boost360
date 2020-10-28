package com.dashboard.constant

import androidx.annotation.LayoutRes
import com.dashboard.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CHANNEL_ITEM_VIEW,
  BUSINESS_SETUP_ITEM_VIEW,
  MANAGE_BUSINESS_ITEM_VIEW,
  QUICK_ACTION_ITEM_VIEW,
  RIA_ACADEMY_ITEM_VIEW,
  BOOST_PREMIUM_ITEM_VIEW,
  ROI_SUMMARY_ITEM_VIEW,
  GROWTH_STATE_ITEM_VIEW;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      CHANNEL_ITEM_VIEW -> R.layout.item_channel_d
      BUSINESS_SETUP_ITEM_VIEW -> R.layout.item_business_management
      MANAGE_BUSINESS_ITEM_VIEW -> R.layout.item_manage_business_d
      QUICK_ACTION_ITEM_VIEW -> R.layout.item_quick_action
      RIA_ACADEMY_ITEM_VIEW -> R.layout.item_learn_digital_journey
      BOOST_PREMIUM_ITEM_VIEW -> R.layout.item_boost_premium
      ROI_SUMMARY_ITEM_VIEW -> R.layout.item_roi_summary
      GROWTH_STATE_ITEM_VIEW -> R.layout.item_growth_state
    }
  }
}

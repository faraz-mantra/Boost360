package com.dashboard.constant

import androidx.annotation.LayoutRes
import com.dashboard.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CHANNEL_ITEM_VIEW,
  BUSINESS_SETUP_ITEM_VIEW,
  BUSINESS_SETUP_HIGH_ITEM_VIEW,
  MANAGE_BUSINESS_ITEM_VIEW,
  QUICK_ACTION_ITEM_VIEW,
  RIA_ACADEMY_ITEM_VIEW,
  RIA_ACADEMY_ITEM_NEW_VIEW,
  BOOST_PREMIUM_ITEM_VIEW,
  BOOST_CUSTOMER_ITEM_VIEW,
  ROI_SUMMARY_ITEM_VIEW,
  GROWTH_STATE_ITEM_VIEW,
  BUSINESS_CONTENT_SETUP_ITEM_VIEW,
  ITEMS_CONTENT_SETUP_ITEM_VIEW,
  ALL_BOOST_ADD_ONS_VIEW,
  HOME_DRAWER_VIEW;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      CHANNEL_ITEM_VIEW -> R.layout.item_channel_d
      BUSINESS_SETUP_ITEM_VIEW -> R.layout.item_business_management
      BUSINESS_SETUP_HIGH_ITEM_VIEW -> R.layout.item_business_setup_high
      MANAGE_BUSINESS_ITEM_VIEW -> R.layout.item_manage_business_d
      QUICK_ACTION_ITEM_VIEW -> R.layout.item_quick_action
      RIA_ACADEMY_ITEM_VIEW -> R.layout.item_ria_academy
      RIA_ACADEMY_ITEM_NEW_VIEW -> R.layout.item_ria_academy_new
      BOOST_PREMIUM_ITEM_VIEW -> R.layout.item_boost_premium
      BOOST_CUSTOMER_ITEM_VIEW -> R.layout.item_customer_patient_item
      ROI_SUMMARY_ITEM_VIEW -> R.layout.item_roi_summary
      GROWTH_STATE_ITEM_VIEW -> R.layout.item_growth_state
      BUSINESS_CONTENT_SETUP_ITEM_VIEW -> R.layout.item_business_content_setup
      ITEMS_CONTENT_SETUP_ITEM_VIEW -> R.layout.item_content_setup_manage
      ALL_BOOST_ADD_ONS_VIEW -> R.layout.item_boost_add_ons
      HOME_DRAWER_VIEW -> R.layout.item_drawer_view
    }
  }
}

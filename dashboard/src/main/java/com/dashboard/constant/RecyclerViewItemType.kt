package com.dashboard.constant

import androidx.annotation.LayoutRes
import com.dashboard.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CHANNEL_ITEM_VIEW,
  CHANNEL_STATUS_ITEM_VIEW,
  BUSINESS_SETUP_ITEM_VIEW,
  BUSINESS_SETUP_HIGH_ITEM_VIEW,
  MANAGE_BUSINESS_ITEM_VIEW,
  QUICK_ACTION_ITEM_VIEW,
  RIA_ACADEMY_ITEM_VIEW,
  BOOST_PREMIUM_ITEM_VIEW,
  BOOST_ENQUIRIES_ITEM_VIEW,
  BOOST_WEBSITE_ITEM_VIEW,
  ROI_SUMMARY_ITEM_VIEW,
  GROWTH_STATE_ITEM_VIEW,
  BUSINESS_CONTENT_SETUP_ITEM_VIEW,
  ITEMS_CONTENT_SETUP_ITEM_VIEW,
  ALL_BOOST_ADD_ONS_VIEW,
  WEBSITE_COLOR_VIEW,
  WEBSITE_FONT_VIEW,
  RECYCLER_USEFUL_LINKS,
  RECYCLER_ABOUT_APP,
  BOOST_WEBSITE_ITEM_FEATURE_VIEW,
  RECYCLER_WEBSITE_NAV,
  CONSULTATION_VIEW,
  FILTER_DATE_VIEW,
  OPTIONAL_TASKS_VIEW,
  POST_PURCHASE_1_VIEW,
  POST_PURCHASE_2_VIEW,
  READINESS_SCORE_1_VIEW,
  READINESS_SCORE_2_VIEW;

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
      BOOST_PREMIUM_ITEM_VIEW -> R.layout.item_boost_premium
      BOOST_ENQUIRIES_ITEM_VIEW -> R.layout.item_customer_patient_item
      BOOST_WEBSITE_ITEM_VIEW -> R.layout.item_website_item
      BOOST_WEBSITE_ITEM_FEATURE_VIEW -> R.layout.website_item_feature
      ROI_SUMMARY_ITEM_VIEW -> R.layout.item_roi_summary
      GROWTH_STATE_ITEM_VIEW -> R.layout.item_growth_state
      BUSINESS_CONTENT_SETUP_ITEM_VIEW -> R.layout.item_business_content_setup
      ITEMS_CONTENT_SETUP_ITEM_VIEW -> R.layout.item_content_setup_manage
      ALL_BOOST_ADD_ONS_VIEW -> R.layout.item_boost_add_ons
      FILTER_DATE_VIEW -> R.layout.item_filter_date
      CONSULTATION_VIEW -> R.layout.recycler_item_consultation
      WEBSITE_COLOR_VIEW -> R.layout.recycler_item_colors
      WEBSITE_FONT_VIEW -> R.layout.recycler_item_select_font
      RECYCLER_USEFUL_LINKS -> R.layout.recycler_item_useful_links
      RECYCLER_ABOUT_APP -> R.layout.recycler_item_about_app
      RECYCLER_WEBSITE_NAV -> R.layout.recycler_item_website_nav
      CHANNEL_STATUS_ITEM_VIEW -> R.layout.item_social_media
      OPTIONAL_TASKS_VIEW -> R.layout.item_optional_tasks
      POST_PURCHASE_1_VIEW -> R.layout.item_post_purchase_1
      POST_PURCHASE_2_VIEW -> R.layout.item_post_purchase_2
      READINESS_SCORE_1_VIEW -> R.layout.item_readiness_score_1
      READINESS_SCORE_2_VIEW -> R.layout.item_readiness_score_2
    }
  }
}

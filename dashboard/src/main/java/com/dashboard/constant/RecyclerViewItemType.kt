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

  //My To Do List Cards View Holders
  OPTIONAL_TASKS_VIEW,
  POST_PURCHASE_1_VIEW,
  POST_PURCHASE_2_VIEW,
  MY_TODO_LIST_ACTION,
  READINESS_SCORE_2_VIEW,
  RENEWAL_1_VIEW,
  RENEWAL_2_VIEW,
  RENEWAL_3_VIEW,
  NEW_SINGLE_FEATURE_VIEW,
  NEW_MULTIPLE_FEATURE_VIEW,
  CONTINUE_WHERE_LEFT_VIEW,
  MISSED_CALL_1_VIEW,
  MISSED_CALL_2_VIEW,
  MISSED_CALL_3_VIEW,
  MISSED_CALL_4_VIEW,
  ORDER_DETAILS_VIEW,
  ATTENTION_ORDER_ALERT_VIEW,
  SMS_EMAIL_ENQUIRY_VIEW;

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

      //My To Do List Cards View Holders
      OPTIONAL_TASKS_VIEW -> R.layout.item_optional_tasks
      POST_PURCHASE_1_VIEW -> R.layout.item_post_purchase_1
      POST_PURCHASE_2_VIEW -> R.layout.item_post_purchase_2
      MY_TODO_LIST_ACTION -> R.layout.item_todo_list_action
      READINESS_SCORE_2_VIEW -> R.layout.item_readiness_score_2
      RENEWAL_1_VIEW -> R.layout.item_renewal_1
      RENEWAL_2_VIEW -> R.layout.item_renewal_2
      RENEWAL_3_VIEW -> R.layout.item_renewal_3
      NEW_SINGLE_FEATURE_VIEW -> R.layout.item_new_single_feature
      NEW_MULTIPLE_FEATURE_VIEW -> R.layout.item_new_multiple_feature
      CONTINUE_WHERE_LEFT_VIEW -> R.layout.item_continue_where_left
      MISSED_CALL_1_VIEW -> R.layout.item_missed_call_1
      MISSED_CALL_2_VIEW -> R.layout.item_missed_call_2
      MISSED_CALL_3_VIEW -> R.layout.item_missed_call_3
      MISSED_CALL_4_VIEW -> R.layout.item_missed_call_4
      ORDER_DETAILS_VIEW -> R.layout.item_order_details_card
      ATTENTION_ORDER_ALERT_VIEW -> R.layout.item_attention_order_alert
      SMS_EMAIL_ENQUIRY_VIEW -> R.layout.item_sms_email_enquiry
    }
  }
}

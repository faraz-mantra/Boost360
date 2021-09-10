package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  ORDERS_ITEM_TYPE,
  INVENTORY_ORDER_ITEM,
  ITEM_ORDER_DETAILS,
  ITEM_DELIVERY_OPTIONS,
  APPOINTMENT_ITEM_TYPE,
  APPOINTMENT_SPA_ITEM_TYPE,
  APPOINTMENT_DETAILS,
  APPOINTMENT_SPA_DETAILS,
  ITEM_SERVICE_LOCATIONS,
  ITEM_CHOOSE_PURPOSE,
  VIDEO_CONSULT_ITEM_TYPE,
  VIDEO_CONSULT_DETAILS,
  DATE_VIEW_TYPE,
  APPOINTMENT_SCHEDULE,
  GENDER_SELECTION,
  APPOINTMENT_TYPE,
  PICK_INVENTORY_NATURE,
  FILTER_ORDER_ITEM,
  TIME_SLOT_ITEM,
  WEEK_TIMING_SELECTED,
  SERVICES_DEPARTMENT,
  ORDER_MENU_ITEM,
  PRODUCT_ITEM,
  PRODUCT_ITEM_SELECTED,
  PRODUCT_BOTTOM_SHEET_OPTIONS,
  STAFF_ITEM, ITEM_FAQ, ITEM_VIDEO,
  SLOTS_ITEM,
  SLOTS_ITEM_NEW;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_order_loader
      ORDERS_ITEM_TYPE -> R.layout.item_order_type
      INVENTORY_ORDER_ITEM -> R.layout.item_order
      ITEM_ORDER_DETAILS -> R.layout.item_order_details
      ITEM_DELIVERY_OPTIONS -> R.layout.item_bottom_sheet_pick_up_delivery_option
      APPOINTMENT_ITEM_TYPE -> R.layout.item_appointments_order
      APPOINTMENT_SPA_ITEM_TYPE -> R.layout.item_appointments_spa
      APPOINTMENT_DETAILS -> R.layout.item_booking_details
      APPOINTMENT_SPA_DETAILS -> R.layout.item_appointment_spa_details
      ITEM_SERVICE_LOCATIONS -> R.layout.item_bottom_sheet_service_locations
      ITEM_CHOOSE_PURPOSE -> R.layout.item_bottom_sheet_choose_purpose
      DATE_VIEW_TYPE -> R.layout.item_date_view
      VIDEO_CONSULT_ITEM_TYPE -> R.layout.item_video_consult_order
      VIDEO_CONSULT_DETAILS -> R.layout.item_video_consult_details
      APPOINTMENT_SCHEDULE -> R.layout.item_appointment_schedule
      GENDER_SELECTION -> R.layout.item_bottom_sheet_select_gender
      APPOINTMENT_TYPE -> R.layout.item_bottom_sheet_appointment_type
      PICK_INVENTORY_NATURE -> R.layout.item_bottom_sheet_pick_inventory_nature
      FILTER_ORDER_ITEM -> R.layout.item_bottom_sheet_filter
      TIME_SLOT_ITEM -> R.layout.item_bottom_time_slot
      WEEK_TIMING_SELECTED -> R.layout.item_week_time_select
      SERVICES_DEPARTMENT -> R.layout.item_consultation_services
      ORDER_MENU_ITEM -> R.layout.item_order_menu
      PRODUCT_ITEM -> R.layout.item_product
      PRODUCT_ITEM_SELECTED -> R.layout.item_products_added
      PRODUCT_BOTTOM_SHEET_OPTIONS -> R.layout.bottom_sheet_order_option
      STAFF_ITEM -> R.layout.staff_item
      SLOTS_ITEM -> R.layout.item_time_slot_appointment
      ITEM_FAQ -> R.layout.item_faq
      ITEM_VIDEO -> R.layout.recycler_item_list_videos
      SLOTS_ITEM_NEW -> R.layout.item_time_slot_appointment_new
    }
  }
}

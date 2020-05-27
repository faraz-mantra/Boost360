package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  ORDERS_ITEM_TYPE,
  INVENTORY_ORDER_ITEM,
  ITEM_ORDER_DETAILS,
  ITEM_DELIVERY_OPTIONS,
  BOOKING_DETAILS,
  ITEM_SERVICE_LOCATIONS,
  ITEM_CHOOSE_PURPOSE,
  BOOKINGS_ITEM_TYPE,
  BOOKINGS_DATE_TYPE,
  APPOINTMENT_SCHEDULE,
  GENDER_SELECTION,
  APPOINTMENT_TYPE,
  PICK_INVENTORY_NATURE
  ;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      ORDERS_ITEM_TYPE -> R.layout.item_order_type
      INVENTORY_ORDER_ITEM -> R.layout.item_order
      ITEM_ORDER_DETAILS -> R.layout.item_order_details
      ITEM_DELIVERY_OPTIONS -> R.layout.item_bottom_sheet_pick_up_delivery_option
      BOOKING_DETAILS -> R.layout.item_booking_details
      ITEM_SERVICE_LOCATIONS -> R.layout.item_bottom_sheet_service_locations
      BOOKINGS_ITEM_TYPE -> R.layout.item_bookings_all_order
      ITEM_CHOOSE_PURPOSE -> R.layout.item_bottom_sheet_choose_purpose
      BOOKINGS_DATE_TYPE -> R.layout.item_bookings_date
      APPOINTMENT_SCHEDULE -> R.layout.item_appointment_schedule
      GENDER_SELECTION -> R.layout.item_bottom_sheet_select_gender
      APPOINTMENT_TYPE -> R.layout.item_bottom_sheet_appointment_type
      PICK_INVENTORY_NATURE -> R.layout.item_bottom_sheet_pick_inventory_nature
    }
  }
}

package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  ORDER_ITEM_TYPE,
  INVENTORY_ORDER_ITEM,
  ITEM_ORDER_DETAILS,
  ITEM_DELIVERY_OPTIONS,
  BOOKING_DETAILS,
  ITEM_SERVICE_LOCATIONS
  ;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      ORDER_ITEM_TYPE -> R.layout.item_order_type
      INVENTORY_ORDER_ITEM -> R.layout.item_order
      ITEM_ORDER_DETAILS -> R.layout.item_order_details
      ITEM_DELIVERY_OPTIONS -> R.layout.item_bottom_sheet_pick_up_delivery_option
      BOOKING_DETAILS -> R.layout.item_booking_details
      ITEM_SERVICE_LOCATIONS -> R.layout.item_bottom_sheet_service_locations
    }
  }
}

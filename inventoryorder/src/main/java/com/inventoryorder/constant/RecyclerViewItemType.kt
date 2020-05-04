package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  ORDER_ITEM_TYPE,
  INVENTORY_ORDER_ITEM,
  ITEM_ORDER_DETAILS,
  ITEM_DELIVERY_OPTIONS
  ;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      ORDER_ITEM_TYPE -> R.layout.item_order_type
      INVENTORY_ORDER_ITEM -> R.layout.item_order
      ITEM_ORDER_DETAILS -> R.layout.item_order_details
      ITEM_DELIVERY_OPTIONS -> R.layout.item_bottom_sheet_pick_up_delivery_option
    }
  }
}

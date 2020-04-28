package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  ORDER_ITEM_TYPE,
  INVENTORY_ORDER_ITEM;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      ORDER_ITEM_TYPE -> R.layout.item_order_type
      INVENTORY_ORDER_ITEM -> R.layout.item_order
    }
  }
}

package com.inventoryorder.constant

import androidx.annotation.LayoutRes
import com.inventoryorder.R

enum class RecyclerViewItemType {
  ORDER_ITEM;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      ORDER_ITEM -> R.layout.item_order
    }
  }
}

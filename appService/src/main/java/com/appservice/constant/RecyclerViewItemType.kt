package com.appservice.constant

import androidx.annotation.LayoutRes
import com.appservice.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  SPECIFICATION_ITEM;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      SPECIFICATION_ITEM -> R.layout.row_layout_added_specs
    }
  }
}

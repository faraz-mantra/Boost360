package com.dashboard.constant

import androidx.annotation.LayoutRes
import com.dashboard.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CHANNEL_ITEM_VIEW;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      CHANNEL_ITEM_VIEW -> R.layout.item_channel_d
    }
  }
}

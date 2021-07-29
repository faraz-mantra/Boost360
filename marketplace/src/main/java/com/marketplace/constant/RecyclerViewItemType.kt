package com.marketplace.constant

import androidx.annotation.LayoutRes
import com.marketplace.R


enum class RecyclerViewItemType {
  PAGINATION_LOADER;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader_mp

    }
  }
}

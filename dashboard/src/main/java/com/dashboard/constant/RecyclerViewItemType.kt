package com.dashboard.constant

import androidx.annotation.LayoutRes
import com.dashboard.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
    }
  }
}

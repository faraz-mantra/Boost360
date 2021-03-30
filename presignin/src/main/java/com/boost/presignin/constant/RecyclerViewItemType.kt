package com.boost.presignin.constant

import androidx.annotation.LayoutRes
import com.boost.presignin.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
    }
  }
}

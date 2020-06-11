package com.catlogservice.constant

import androidx.annotation.LayoutRes
import com.catlogservice.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.fragment_service_detail
    }
  }
}

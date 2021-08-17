package com.marketplace.constant

import androidx.annotation.LayoutRes
import com.marketplace.R


enum class RecyclerViewItemType {
  PROMO_BANNER_ITEM_VIEW,
  PAGINATION_LOADER;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader_mp
      PROMO_BANNER_ITEM_VIEW -> R.layout.mp_promo_banner_item
    }
  }
}

package com.boost.marketplace.constant

import androidx.annotation.LayoutRes
import com.boost.marketplace.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  BUSINESS_SETUP_ITEM_VIEW,
  ALL_BOOST_ADD_ONS_VIEW,
  MANAGE_BUSINESS_ITEM_VIEW;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      BUSINESS_SETUP_ITEM_VIEW -> R.layout.item_business_management_d
      ALL_BOOST_ADD_ONS_VIEW -> R.layout.item_boost_add_ons
      MANAGE_BUSINESS_ITEM_VIEW -> R.layout.item_manage_business
    }
  }
}

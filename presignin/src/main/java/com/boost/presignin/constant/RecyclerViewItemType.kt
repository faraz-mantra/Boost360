package com.boost.presignin.constant

import androidx.annotation.LayoutRes
import com.boost.presignin.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CATEGORY_ITEM, BUSINESS_LIST_ITEM,
  SECTION_HEADER_ITEM;


  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      CATEGORY_ITEM -> R.layout.item_category_layout
      SECTION_HEADER_ITEM -> R.layout.item_section_header_layout
      BUSINESS_LIST_ITEM -> R.layout.recycler_item_fp_info
    }
  }
}

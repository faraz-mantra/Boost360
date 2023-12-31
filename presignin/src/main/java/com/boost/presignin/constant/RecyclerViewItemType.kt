package com.boost.presignin.constant

import androidx.annotation.LayoutRes
import com.boost.presignin.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  CATEGORY_ITEM,
  CATEGORY_SUGGESTION_ITEM,
  CATEGORY_ITEM_OV2,
  BUSINESS_LIST_ITEM,
  SECTION_HEADER_ITEM,
  INTRO_NEW_SLIDES;


  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      CATEGORY_ITEM -> R.layout.item_category_layout
      SECTION_HEADER_ITEM -> R.layout.item_section_header_layout
      BUSINESS_LIST_ITEM -> R.layout.recycler_item_fp_info
      INTRO_NEW_SLIDES -> R.layout.item_intro_new_slides
      CATEGORY_ITEM_OV2->R.layout.item_website_categories
      CATEGORY_SUGGESTION_ITEM->R.layout.list_item_category_suggestion
    }
  }
}

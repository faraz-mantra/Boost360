package com.appservice.constant

import androidx.annotation.LayoutRes
import com.appservice.R

enum class RecyclerViewItemType {
  PAGINATION_LOADER,
  SPECIFICATION_ITEM,
  IMAGE_PREVIEW,
  GST_DETAILS_VIEW,
  SESSION_ITEM_VIEW,
  SERVICE_TIMING_ITEM_VIEW,
  UPDATE_BUSINESS_ITEM_VIEW,
  CREATE_CATEGORY_ITEM_VIEW,
  ADDITIONAL_FILE_VIEW,
  SERVICE_ITEM_VIEW,
  STAFF_LISTING_VIEW,
  EXPERIENCE_RECYCLER_ITEM,
  SERVICE_LISTING_VIEW,
  OFFER_LISTING_VIEW,
  OFFER_SELECT_SERVICES,
  STAFF_FILTER_VIEW;


  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      SPECIFICATION_ITEM -> R.layout.row_layout_added_specs
      IMAGE_PREVIEW -> R.layout.item_preview_image
      GST_DETAILS_VIEW -> R.layout.item_gst_detail
      SESSION_ITEM_VIEW -> R.layout.recycler_item_session
      SERVICE_TIMING_ITEM_VIEW -> R.layout.item_service_timing
      UPDATE_BUSINESS_ITEM_VIEW -> R.layout.item_updates_list
      CREATE_CATEGORY_ITEM_VIEW -> R.layout.item_create_category
      ADDITIONAL_FILE_VIEW -> R.layout.item_pdf_file
      SERVICE_ITEM_VIEW -> R.layout.recycler_item_service
      STAFF_LISTING_VIEW -> R.layout.recycler_item_staff_listing
      EXPERIENCE_RECYCLER_ITEM -> R.layout.item_experience_details
      SERVICE_LISTING_VIEW -> R.layout.recycler_item_service_listing
      STAFF_FILTER_VIEW -> R.layout.recycler_item_staff_filter
      OFFER_LISTING_VIEW -> R.layout.recycler_item_offer
      OFFER_SELECT_SERVICES -> R.layout.recycler_item_service_select_offer
    }
  }
}

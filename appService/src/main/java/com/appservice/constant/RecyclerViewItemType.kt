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
  SERVICE_CATEGORY_ITEM_VIEW,
  ADDITIONAL_FILE_VIEW,
  SERVICE_ITEM_VIEW,
  STAFF_LISTING_VIEW,
  EXPERIENCE_RECYCLER_ITEM,
  SERVICE_LISTING_VIEW,
  OFFER_LISTING_VIEW,
  PRODUCT_CATEGORY_ITEM_VIEW,
  PRODUCT_LISTING,
  OFFER_SELECT_SERVICES,
  STAFF_FILTER_VIEW,
  CATALOG_SETTING_TILES,
  DOMAIN_STEPS,
  DOMAIN_NAME_SUGGESTIONS,
  SIMILAR_DOMAIN_SUGGESTIONS,
  BACKGROUND_IMAGE_RV,
  BACKGROUND_IMAGE_FULL_SCREEN,
  GST_SLAB_SETTING,
  VMN_CALL,
  TESTIMONIAL_ITEM,
  PAST_UPDATE_ITEM,
  PAST_POST_CATEGORIES,
  PAST_TAGS,
  PAST_SOCIAL_ICON_LIST_ITEM;


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
      SERVICE_CATEGORY_ITEM_VIEW -> R.layout.recycler_item_service_category
      PRODUCT_CATEGORY_ITEM_VIEW -> R.layout.recycler_item_product_category
      PRODUCT_LISTING -> R.layout.recycler_item_product_listing
      CATALOG_SETTING_TILES -> R.layout.recycler_item_ecom_apt_settings
      DOMAIN_STEPS -> R.layout.list_item_steps_domain
      DOMAIN_NAME_SUGGESTIONS -> R.layout.item_domain_suggestions
      SIMILAR_DOMAIN_SUGGESTIONS -> R.layout.item_similar_domain_suggestions
      BACKGROUND_IMAGE_RV -> R.layout.list_item_background_images
      BACKGROUND_IMAGE_FULL_SCREEN -> R.layout.list_item_bg_image_full_screen
      GST_SLAB_SETTING -> R.layout.item_gst_slab
      VMN_CALL -> R.layout.single_item_vmn_call_item_v2
      TESTIMONIAL_ITEM -> R.layout.item_testimonial_list
      PAST_UPDATE_ITEM->R.layout.list_item_past_update
      PAST_POST_CATEGORIES->R.layout.list_item_past_category
      PAST_TAGS->R.layout.list_item_past_tags
      PAST_SOCIAL_ICON_LIST_ITEM->R.layout.list_item_social_icon
    }
  }
}

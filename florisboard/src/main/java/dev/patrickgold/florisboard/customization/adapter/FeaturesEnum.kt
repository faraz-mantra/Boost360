package dev.patrickgold.florisboard.customization.adapter

import androidx.annotation.LayoutRes
import dev.patrickgold.florisboard.R

enum class FeaturesEnum {
  LOADER,
  PRODUCTS,
  UPDATES,
  PHOTOS,
  DETAILS;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      LOADER -> R.layout.pagination_loader
      UPDATES -> R.layout.adapter_item_update
      PRODUCTS -> R.layout.adapter_item_product_new
      PHOTOS -> R.layout.adapter_item_photos
      DETAILS -> R.layout.adapter_item_details
    }
  }
}
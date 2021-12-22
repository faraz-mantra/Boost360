package com.boost.marketplace.infra.constant

import androidx.annotation.LayoutRes
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType.*
import com.boost.marketplace.R


  @LayoutRes
  fun getLayout(type: RecyclerViewItemType): Int {
    return when (type) {
      PAGINATION_LOADER -> R.layout.pagination_loader
      PROMO_BANNER ->R.layout.item_promo_banner
      FEATURES_BY_CATEGORY ->R.layout.item_features_by_category
      PACKS -> TODO()
      VIDEOS -> TODO()
      PARTNER -> TODO()
      MARKETPLACE_OFFERS ->R.layout.item_marketplaceoffers_info
      FEATURES_MODEL ->R.layout.item_myplan_features
      RESULT -> R.layout.item_order_history
      PACKS_BUNDLES -> R.layout.item_packs_list
    }
  }

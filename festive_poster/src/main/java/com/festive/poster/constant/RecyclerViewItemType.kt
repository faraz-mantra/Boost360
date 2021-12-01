package com.festive.poster.constant

import androidx.annotation.LayoutRes
import com.festive.poster.R

enum class RecyclerViewItemType {
  POSTER_PACK,
  POSTER,
  POSTER_PACK_PURCHASED,
  POSTER_SHARE,
  TODAYS_PICK_TEMPLATE_VIEW,
  TEMPLATE_VIEW,
  SOCIAL_CONN,
  BROWSE_ALL_TEMPLATE_CAT;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      POSTER_PACK -> R.layout.list_item_poster_pack
      POSTER -> R.layout.list_item_poster
      POSTER_PACK_PURCHASED -> R.layout.list_item_purchased_poster_pack
      POSTER_SHARE -> R.layout.list_item_poster_share
      TODAYS_PICK_TEMPLATE_VIEW -> R.layout.list_item_todays_pick_template
      TEMPLATE_VIEW -> R.layout.list_item_template
      SOCIAL_CONN -> R.layout.list_item_social_conn
      BROWSE_ALL_TEMPLATE_CAT->R.layout.list_item_browse_template_cat
    }
  }
}

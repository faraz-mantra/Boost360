package com.festive.poster.constant

import androidx.annotation.LayoutRes
import com.festive.poster.R

enum class RecyclerViewItemType {
  POSTER_PACK,
  POSTER,
  POSTER_PACK_DOWNLOADS;
  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      POSTER_PACK->R.layout.list_item_poster_pack
      POSTER->R.layout.list_item_poster
      POSTER_PACK_DOWNLOADS->R.layout.list_item_downloads_poster
    }
  }
}

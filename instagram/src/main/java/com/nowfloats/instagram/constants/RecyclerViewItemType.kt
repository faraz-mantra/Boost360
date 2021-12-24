package com.nowfloats.instagram.constants

import androidx.annotation.LayoutRes
import com.nowfloats.instagram.R

enum class RecyclerViewItemType {
  IG_FEATURES;

  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
        IG_FEATURES->R.layout.list_item_ig_features
    }
  }
}

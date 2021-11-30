package com.example.template.constant

import androidx.annotation.LayoutRes
import com.example.template.R

enum class RecyclerViewItemType {
  TODAYS_PICK_TEMPLATE_VIEW,
  TEMPLATE_VIEW,
  SOCIAL_CONN;

  @LayoutRes
  fun getLayout(): Int {
    /*return when (this) {
      TODAYS_PICK_TEMPLATE_VIEW -> R.layout.list_item_todays_pick_template
      TEMPLATE_VIEW -> R.layout.list_item_template
      SOCIAL_CONN->R.layout.list_item_social_conn

    }*/
    return 0
  }
}

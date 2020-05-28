package com.framework.models

import java.io.Serializable

interface BaseRecyclerViewItem : Serializable {
  fun getRecyclerViewType(): Int
}

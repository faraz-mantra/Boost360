package com.inventoryorder.recyclerView

import android.view.View

interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
  fun onItemClickView(position: Int, view: View, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}
package com.addons.recyclerView

import com.addons.recyclerView.BaseRecyclerViewItem


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
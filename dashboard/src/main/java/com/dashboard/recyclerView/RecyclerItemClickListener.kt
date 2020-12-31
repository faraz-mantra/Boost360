package com.dashboard.recyclerView


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
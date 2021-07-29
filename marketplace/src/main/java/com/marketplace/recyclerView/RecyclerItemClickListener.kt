package com.marketplace.recyclerView


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
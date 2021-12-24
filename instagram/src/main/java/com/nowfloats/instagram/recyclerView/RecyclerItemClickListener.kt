package com.nowfloats.instagram.recyclerView

import com.nowfloats.instagram.recyclerView.BaseRecyclerViewItem


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
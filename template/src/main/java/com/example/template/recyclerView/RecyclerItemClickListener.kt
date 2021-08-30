package com.example.template.recyclerView

import com.example.template.recyclerView.BaseRecyclerViewItem


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
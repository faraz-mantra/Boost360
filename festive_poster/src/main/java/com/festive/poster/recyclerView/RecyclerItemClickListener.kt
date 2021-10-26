package com.festive.poster.recyclerView

import com.festive.poster.recyclerView.BaseRecyclerViewItem


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
  fun onChildClick(childPosition:Int,parentPosition: Int, childItem: BaseRecyclerViewItem?, parentItem: BaseRecyclerViewItem?, actionType: Int) {

  }
}
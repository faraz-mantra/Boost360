package com.onboarding.nowfloats.recyclerView

interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int)
}
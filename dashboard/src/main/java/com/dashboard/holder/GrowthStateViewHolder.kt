package com.dashboard.holder

import com.dashboard.R
import com.dashboard.databinding.ItemGrowthStateBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class GrowthStateViewHolder(binding: ItemGrowthStateBinding) : AppBaseRecyclerViewHolder<ItemGrowthStateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    getColor(if (position % 2 == 0) R.color.light_grey_4 else R.color.white)?.let { binding.mainContent.setBackgroundColor(it) }
  }

}

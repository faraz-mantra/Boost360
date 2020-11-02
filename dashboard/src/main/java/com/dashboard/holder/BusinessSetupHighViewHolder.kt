package com.dashboard.holder

import com.dashboard.databinding.ItemBusinessSetupHighBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessSetupHighViewHolder(binding: ItemBusinessSetupHighBinding) : AppBaseRecyclerViewHolder<ItemBusinessSetupHighBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    if (position % 2 != 0) {
      binding.viewReadinessScore.visible()
      binding.viewBusinessCount.gone()
    } else {
      binding.viewReadinessScore.gone()
      binding.viewBusinessCount.visible()
    }
  }
}

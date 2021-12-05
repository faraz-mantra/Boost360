package com.boost.marketplace.holder

import com.boost.marketplace.databinding.ItemBusinessManagementBinding
import com.boost.marketplace.recyclerView.AppBaseRecyclerViewHolder
import com.boost.marketplace.recyclerView.BaseRecyclerViewItem

class BusinessSetupViewHolder(binding: ItemBusinessManagementBinding) :
  AppBaseRecyclerViewHolder<ItemBusinessManagementBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)

  }

  private fun startCheckAnimation(isAnimate: Boolean) {
    binding.lottySyncOk.apply { if (isAnimate) playAnimation() else pauseAnimation() }
  }
}

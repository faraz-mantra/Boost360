package com.boost.marketplace.holder

import com.boost.marketplace.databinding.ItemBusinessManagementDBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewHolder
import com.boost.marketplace.recyclerView.BaseRecyclerViewItem

class BusinessSetupViewHolder(binding: ItemBusinessManagementDBinding) :
  AppBaseRecyclerViewHolder<ItemBusinessManagementDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)

  }

  private fun startCheckAnimation(isAnimate: Boolean) {
    binding.lottySyncOk.apply { if (isAnimate) playAnimation() else pauseAnimation() }
  }
}

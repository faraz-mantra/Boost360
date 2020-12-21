package com.dashboard.holder

import com.dashboard.databinding.ItemBoostPremiumBinding
import com.dashboard.model.live.premiumBanner.PromoBanner
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad
import com.inventoryorder.R

class BoostPremiumViewHolder(binding: ItemBoostPremiumBinding) : AppBaseRecyclerViewHolder<ItemBoostPremiumBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? PromoBanner ?: return
    activity?.glideLoad(binding.image, data.image?.url ?: "", R.drawable.placeholder_image)
  }

}

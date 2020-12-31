package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBoostPremiumBinding
import com.dashboard.model.live.premiumBanner.PromoAcademyBanner
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad
import com.inventoryorder.R

class BoostPremiumViewHolder(binding: ItemBoostPremiumBinding) : AppBaseRecyclerViewHolder<ItemBoostPremiumBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? PromoAcademyBanner ?: return
    activity?.glideLoad(binding.image, data.image?.url ?: "", R.drawable.placeholder_image_n)
    binding.maimView.setOnClickListener { listener?.onItemClick(position,data, RecyclerViewActionType.PROMO_BANNER_CLICK.ordinal) }
  }

}

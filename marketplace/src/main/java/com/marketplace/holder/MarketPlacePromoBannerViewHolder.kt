package com.marketplace.holder

import com.marketplace.databinding.MpPromoBannerItemBinding
import com.marketplace.model.features.PromoBanner
import com.marketplace.recyclerView.AppBaseRecyclerViewHolder
import com.marketplace.recyclerView.BaseRecyclerViewItem

class MarketPlacePromoBannerViewHolder(binding: MpPromoBannerItemBinding) : AppBaseRecyclerViewHolder<MpPromoBannerItemBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as? PromoBanner ?: return
        binding.mpPromoBannerTitle.text = data.title

    }

}
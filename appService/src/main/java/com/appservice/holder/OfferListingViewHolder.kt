package com.appservice.holder

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemOfferBinding
import com.appservice.offers.models.DataItem
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad

class OfferListingViewHolder(binding: RecyclerItemOfferBinding) : AppBaseRecyclerViewHolder<RecyclerItemOfferBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? DataItem) ?: return
        apply { activity?.glideLoad(binding.rivOfferImage, data.featuredImage?.tileImage, R.drawable.placeholder_image_n) }
        binding.ctvOffersDescription.text = data.description
        binding.ctvOffersHeading.text = data.name
        binding.ctvOffersPricing.text = data.price.toString()
        binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) }
        binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_DATA_SHARE_CLICK.ordinal) }
        binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_WHATS_APP_SHARE.ordinal) }
    }
}
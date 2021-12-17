package com.boost.marketplace.holder

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemPromoBannerBinding
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.marketplace.R
import com.framework.glide.util.glideLoad


class MarketPlacePromoBannerViewHolder(binding: ItemPromoBannerBinding) :AppBaseRecyclerViewHolder <ItemPromoBannerBinding>(binding) {

  override fun bind(position: Int, item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? PromoBanners ?: return
    activity?.glideLoad(binding.packagePrimaryImage, data.image?.url ?: "", R.drawable.placeholder_image_n)
    binding.packagePrimaryImage.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.MARKETPLACE_PROMO_BANNER_CLICK.ordinal
      )
    }

  }

}



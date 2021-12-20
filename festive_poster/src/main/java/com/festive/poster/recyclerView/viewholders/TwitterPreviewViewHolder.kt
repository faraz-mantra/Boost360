package com.festive.poster.recyclerView.viewholders

import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils

class TwitterPreviewViewHolder(binding: SocialPreviewTwitterBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewTwitterBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel

        SvgUtils.loadImage(model.posterModel.url(), binding.ivSvg, model.posterModel.keys,model.posterModel.isPurchased)
        binding.tvCaption.text =model.desc
        binding.tvTitle.text = model.title
        super.bind(position, item)
    }

}

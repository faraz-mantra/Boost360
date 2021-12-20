package com.festive.poster.recyclerView.viewholders

import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewFbBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils

class FBPreviewViewHolder(binding: SocialPreviewFbBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewFbBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        SvgUtils.loadImage(model.posterModel.url(), binding.ivPoster, model.posterModel.keys,model.posterModel.isPurchased)
        binding.tvCaption.text =model.desc
        binding.tvName.text = model.title

        super.bind(position, item)
    }

}

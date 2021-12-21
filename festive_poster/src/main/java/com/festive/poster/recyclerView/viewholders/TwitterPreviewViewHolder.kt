package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.utils.highlightHashTag

class TwitterPreviewViewHolder(binding: SocialPreviewTwitterBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewTwitterBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel

        SvgUtils.loadImage(model.posterModel.url(), binding.ivSvg, model.posterModel.keys,model.posterModel.isPurchased)
        binding.tvCaption.text = highlightHashTag(model.desc, R.color.color4C9EEB)
        binding.tvTitle.text = model.title
        super.bind(position, item)
    }

}

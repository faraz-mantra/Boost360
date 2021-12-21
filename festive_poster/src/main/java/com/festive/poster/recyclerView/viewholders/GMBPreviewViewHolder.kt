package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewGmbBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.utils.highlightHashTag

class GMBPreviewViewHolder(binding: SocialPreviewGmbBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewGmbBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        SvgUtils.loadImage(model.posterModel.url(), binding.ivPoster, model.posterModel.keys,model.posterModel.isPurchased)
        binding.tvCaption.text = highlightHashTag(model.desc, R.color.color4C9EEB)
        binding.tvName.text = model.title
        super.bind(position, item)
    }

}

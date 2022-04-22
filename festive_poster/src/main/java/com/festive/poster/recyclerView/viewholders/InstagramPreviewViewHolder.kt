package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.SocialPreviewInstagramBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.extensions.gone
import com.framework.utils.highlightHashTag
import com.framework.utils.loadUsingGlide
import java.io.File

class InstagramPreviewViewHolder(binding: SocialPreviewInstagramBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewInstagramBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        binding.imageExist = model.posterImg.isNullOrEmpty().not()

        if (model.posterImg.isNullOrEmpty()){
            binding.materialCardView.minimumHeight=400
            binding.ivPoster.gone()
        }else{
            binding.materialCardView.minimumHeight=800
            binding.ivPoster.loadUsingGlide(model.posterImg,false)
        }
        binding.materialCardView.requestLayout()
        binding.tvCaption.text = highlightHashTag(model.desc,R.color.color395996,R.font.regular_medium)
        binding.tvName.text = model.title

        super.bind(position, item)
    }

}

package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.databinding.SocialPreviewWebsiteBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.extensions.gone
import com.framework.utils.highlightHashTag
import java.io.File
import androidx.constraintlayout.widget.ConstraintLayout
import com.framework.utils.loadUsingGlide


class WebsitePreviewViewHolder(binding: SocialPreviewWebsiteBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewWebsiteBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        if (model.posterImg.isNullOrEmpty()){
            binding.materialCardView.minimumHeight=400
            binding.ivSvg.gone()
        }else{
            binding.materialCardView.minimumHeight=800
            binding.ivSvg.loadUsingGlide(model.posterImg,false)
        }
        binding.materialCardView.requestLayout()
        binding.tvCaption.text = highlightHashTag(model.desc, R.color.color868686)

        binding.tvCaption.text =model.desc
        super.bind(position, item)
    }

}

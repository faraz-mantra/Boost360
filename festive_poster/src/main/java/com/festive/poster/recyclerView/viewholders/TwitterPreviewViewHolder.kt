package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.databinding.SocialPreviewTwitterBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.extensions.gone
import com.framework.utils.highlightHashTag
import com.framework.utils.loadFromFile
import java.io.File

class TwitterPreviewViewHolder(binding: SocialPreviewTwitterBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewTwitterBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        if (model.posterImg==null){
            binding.materialCardView.minimumHeight=300
            binding.ivSvg.gone()
        }else{
            binding.materialCardView.minimumHeight=700
            binding.ivSvg.loadFromFile(File(model.posterImg),false)
        }
        binding.materialCardView.requestLayout()
        binding.tvCaption.text = highlightHashTag(model.desc, R.color.color4C9EEB)
        binding.tvTitle.text = model.title
        super.bind(position, item)
    }

}

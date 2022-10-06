package com.festive.poster.recyclerView.viewholders

import androidx.core.view.isVisible
import com.festive.poster.R
import com.festive.poster.databinding.SocialPreviewGmbBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.utils.highlightHashTag
import com.framework.utils.loadFromFile

class GMBPreviewViewHolder(binding: SocialPreviewGmbBinding) :
    AppBaseRecyclerViewHolder<SocialPreviewGmbBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel
        binding.imageExist = model.posterImg.isNullOrEmpty().not()
        binding.ivPoster.loadFromFile(model.posterImg,false)
        binding.tvCaption.isVisible = model.desc.isNullOrEmpty().not()

        binding.tvCaption.text = highlightHashTag(model.desc, R.color.color395996,R.font.regular_medium)
        binding.tvName.text = model.title
        super.bind(position, item)
    }

}

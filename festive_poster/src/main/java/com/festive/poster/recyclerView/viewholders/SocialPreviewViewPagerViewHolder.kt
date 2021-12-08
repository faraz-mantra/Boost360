package com.festive.poster.recyclerView.viewholders

import com.festive.poster.databinding.ItemSocialPreviewViewpagerBinding
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class SocialPreviewViewPagerViewHolder(binding: ItemSocialPreviewViewpagerBinding) :
    AppBaseRecyclerViewHolder<ItemSocialPreviewViewpagerBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPreviewModel

        super.bind(position, item)
    }

}

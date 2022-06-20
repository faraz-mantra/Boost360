package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ListItemSocialIconBinding
import com.festive.poster.models.promoModele.PastSocialModel
import com.festive.poster.models.promoModele.SocialPreviewChannel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PastSocialIconViewHolder(binding: ListItemSocialIconBinding) :
    AppBaseRecyclerViewHolder<ListItemSocialIconBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val pastSocialModel = item as PastSocialModel
        binding.ivSocialPastItem.setImageResource(
            when(pastSocialModel.socialPlatformName){
                SocialPreviewChannel.FACEBOOK.name->{
                    R.drawable.ic_facebook_past
                }
                SocialPreviewChannel.TWITTER.name->{
                    R.drawable.ic_twitter_past
                }
                SocialPreviewChannel.INSTAGRAM.name->{
                    R.drawable.ic_instagram_past
                }
                else -> {
                    R.drawable.bg_grey_stroke_circle
                }
            }
        )
        super.bind(position, item)
    }
}
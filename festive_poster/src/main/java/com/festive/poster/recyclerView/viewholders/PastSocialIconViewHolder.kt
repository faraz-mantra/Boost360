package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.databinding.ListItemSocialIconBinding
import com.festive.poster.models.promoModele.PastSocialChannelUpdated
import com.festive.poster.models.promoModele.PastSocialModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PastSocialIconViewHolder(binding: ListItemSocialIconBinding) :
    AppBaseRecyclerViewHolder<ListItemSocialIconBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val pastSocialModel = item as PastSocialModel
        binding.ivSocialPastItem.setImageResource(
            when(pastSocialModel.socialPlatformName){
                PastSocialChannelUpdated.FACEBOOK.name -> {
                    R.drawable.ic_facebook_past
                }
                PastSocialChannelUpdated.TWITTER.name -> {
                    R.drawable.ic_twitter_past
                }
                PastSocialChannelUpdated.INSTAGRAM.name -> {
                    R.drawable.ic_instagram_past
                }
                PastSocialChannelUpdated.WEBSITE.name -> {
                    R.drawable.ic_past_my_website
                }
                PastSocialChannelUpdated.GOOGLEMYBUSINESS.name -> {
                    R.drawable.ic_google_business
                }
                else -> {
                    R.drawable.bg_grey_stroke_circle
                }
            }
        )
        super.bind(position, item)
    }
}
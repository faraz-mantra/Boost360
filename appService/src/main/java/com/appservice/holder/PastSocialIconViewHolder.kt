package com.appservice.holder

import com.appservice.R
import com.appservice.databinding.ListItemSocialIconBinding
import com.appservice.model.updateBusiness.pastupdates.PastSocialChannelUpdated
import com.appservice.model.updateBusiness.pastupdates.PastSocialModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class PastSocialIconViewHolder(binding: ListItemSocialIconBinding) :
    AppBaseRecyclerViewHolder<ListItemSocialIconBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val pastSocialModel = item as PastSocialModel
        binding.ivSocialPastItem.setImageResource(
            when(pastSocialModel.socialPlatformName){
                PastSocialChannelUpdated.FACEBOOK.name,PastSocialChannelUpdated.FACEBOOK_PAGE.name -> {
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
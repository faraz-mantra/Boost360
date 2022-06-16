package com.festive.poster.ui.promoUpdates.holders

import com.bumptech.glide.Glide
import com.festive.poster.FestivePosterApplication
import com.festive.poster.databinding.ListItemPastUpdateBinding
import com.festive.poster.models.promoModele.PastPostItem
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.utils.DateUtils

class PastUpdateViewHolder(binding: ListItemPastUpdateBinding) :
    AppBaseRecyclerViewHolder<ListItemPastUpdateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val postItem = item as? PastPostItem
        binding.apply {
            Glide.with(FestivePosterApplication.instance).load(postItem?.url).into(ivSocialIcon)
            tvSocialTitle.text = postItem?.message
            if (postItem?.createdOn?.contains("/Date(") == true) {
                tvPostedDate.text = DateUtils.parseDate(postItem.createdOn.replace("/Date(", "").replace(")/", ""), DateUtils.FORMAT_SERVER_TO_LOCAL_7)
            }
        }
        super.bind(position, item)
    }
}
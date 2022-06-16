package com.festive.poster.recyclerView.viewholders

import com.festive.poster.databinding.ListItemPastCategoryBinding
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PastCategoryViewHolder(binding: ListItemPastCategoryBinding) :
    AppBaseRecyclerViewHolder<ListItemPastCategoryBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        //val postItem = item as? PastPostItem
        /*binding.apply {
            Glide.with(FestivePosterApplication.instance).load(postItem?.url).into(ivSocialIcon)
            tvSocialTitle.text = postItem?.message
            if (postItem?.createdOn?.contains("/Date(") == true) {
                tvPostedDate.text = DateUtils.parseDate(postItem.createdOn.replace("/Date(", "").replace(")/", ""), DateUtils.FORMAT_SERVER_TO_LOCAL_7)
            }
        }*/
        super.bind(position, item)
    }
}
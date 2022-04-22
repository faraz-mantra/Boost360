package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.databinding.ListItemPastUpdateBinding
import com.festive.poster.models.promoModele.PastPostItem
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PastUpdateViewHolder(binding: ListItemPastUpdateBinding) :
    AppBaseRecyclerViewHolder<ListItemPastUpdateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val postItem = item as? PastPostItem
        super.bind(position, item)
    }
}
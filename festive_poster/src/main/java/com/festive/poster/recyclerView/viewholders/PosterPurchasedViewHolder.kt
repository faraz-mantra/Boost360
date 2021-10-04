package com.festive.poster.recyclerView.viewholders


import com.festive.poster.databinding.ListItemPurchasedPosterBinding
import com.festive.poster.models.PosterPackPurchasedModel
import com.festive.poster.models.PosterPurchasedModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PosterPurchasedViewHolder(binding: ListItemPurchasedPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPurchasedModel

        super.bind(position, item)
    }


}
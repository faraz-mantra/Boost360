package com.festive.poster.recyclerView.viewholders


import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPurchasedPosterPackBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PosterPackPurchasedViewHolder(binding: ListItemPurchasedPosterPackBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterPackBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPackModel

        binding.root.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_PACK_PURCHASED_CLICK.ordinal)
        }
        super.bind(position, item)
    }


}
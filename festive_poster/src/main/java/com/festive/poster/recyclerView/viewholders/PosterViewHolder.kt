package com.festive.poster.recyclerView.viewholders


import com.festive.poster.databinding.ListItemPosterBinding
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class PosterViewHolder(binding: ListItemPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        super.bind(position, item)
    }
}
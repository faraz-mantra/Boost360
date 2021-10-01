package com.festive.poster.recyclerView.viewholders


import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class PosterPackViewHolder(binding: ListItemPosterPackBinding):
    AppBaseRecyclerViewHolder<ListItemPosterPackBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPackModel
        binding.tvPosterHeading.text = model.title
        binding.tvPrice.text = "Pack of ${model.posterList?.size} posters for ${model.price}"
        model.posterList?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>,it)
            binding.vpPoster.adapter = adapter
            TabLayoutMediator(binding.tabLayout,binding.vpPoster){
                    tab,position->
            }.attach()
        }

        super.bind(position, item)
    }
}
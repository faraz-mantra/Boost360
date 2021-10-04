package com.festive.poster.recyclerView.viewholders


import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.databinding.ListItemDownloadsPosterBinding
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.PosterPackDownloadsModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class PosterPackDownloadsViewHolder(binding: ListItemDownloadsPosterBinding):
    AppBaseRecyclerViewHolder<ListItemDownloadsPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPackDownloadsModel

        super.bind(position, item)
    }


}
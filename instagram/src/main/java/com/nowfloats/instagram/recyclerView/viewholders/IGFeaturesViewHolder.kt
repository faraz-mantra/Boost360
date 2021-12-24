package com.nowfloats.instagram.recyclerView.viewholders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.framework.base.BaseActivity
import com.framework.views.itemdecoration.LineItemDecoration
import com.google.android.material.tabs.TabLayoutMediator
import com.nowfloats.instagram.databinding.ListItemIgFeaturesBinding
import com.nowfloats.instagram.models.IGFeaturesModel
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewHolder
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewItem
import com.nowfloats.instagram.recyclerView.BaseRecyclerViewItem

class IGFeaturesViewHolder(binding: ListItemIgFeaturesBinding) : AppBaseRecyclerViewHolder<ListItemIgFeaturesBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = item as IGFeaturesModel
    binding.ivImg.setImageResource(model.img)
    binding.tvTitle.text  = model.title
  }

}
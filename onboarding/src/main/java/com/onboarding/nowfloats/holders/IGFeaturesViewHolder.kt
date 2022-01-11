package com.onboarding.nowfloats.holders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.framework.base.BaseActivity
import com.framework.views.itemdecoration.LineItemDecoration
import com.google.android.material.tabs.TabLayoutMediator
import com.onboarding.nowfloats.databinding.ListItemIgFeaturesBinding
import com.onboarding.nowfloats.model.IGFeaturesModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class IGFeaturesViewHolder(binding: ListItemIgFeaturesBinding) : AppBaseRecyclerViewHolder<ListItemIgFeaturesBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = item as IGFeaturesModel
    binding.ivImg.setImageResource(model.img)
    binding.tvTitle.text  = model.title
  }

}
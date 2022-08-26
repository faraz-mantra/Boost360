package com.festive.poster.recyclerView.viewholders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.databinding.ListItemUpdateStudioFeaturePurchaseBinding
import com.festive.poster.models.FeaturePurchaseUiModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.views.itemdecoration.LineItemDecoration

class UpdateStudioFeaturePurchaseViewHolder(binding: ListItemUpdateStudioFeaturePurchaseBinding)
  : AppBaseRecyclerViewHolder<ListItemUpdateStudioFeaturePurchaseBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = item as FeaturePurchaseUiModel

    binding.tvAdvanceTitle.text = model.title
    super.bind(position, item)
  }


}
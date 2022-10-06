package com.festive.poster.recyclerView.viewholders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemBrowseTabTemplateCatBinding
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.BrowseTabCategory
import com.festive.poster.models.CategoryUi
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.utils.loadFromUrl
import com.framework.views.itemdecoration.LineItemDecoration
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class BrowseTabPosterCatViewHolder(binding: ListItemBrowseTabTemplateCatBinding) :
  AppBaseRecyclerViewHolder<ListItemBrowseTabTemplateCatBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = item as BrowseTabCategory
    binding.ivIcon.loadFromUrl(model.thumbnailUrl)

    binding.tvTitle.text=model.name
    binding.tvSize.text = "(${model.templates?.size})"
    binding.root.setOnClickListener {
      listener?.onItemClick(position,model,RecyclerViewActionType.BROWSE_TAB_POSTER_CAT_CLICKED.ordinal)
    }

    super.bind(position, item)
  }

}
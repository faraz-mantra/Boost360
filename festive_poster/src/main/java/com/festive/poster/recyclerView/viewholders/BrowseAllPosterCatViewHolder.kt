package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemBrowseAllCatBinding
import com.festive.poster.models.BrowseAllCategory
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.utils.loadFromUrl

class BrowseAllPosterCatViewHolder(binding: ListItemBrowseAllCatBinding) : AppBaseRecyclerViewHolder<ListItemBrowseAllCatBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = (item as? BrowseAllCategory) ?: return
    binding.ivIcon.loadFromUrl(model.thumbnailUrl)
    if (model.isSelected) {
      getColor(R.color.color4ACDFF)?.let {
        binding.borderCard.strokeColor = it
      }
      binding.root.alpha = 1F
    } else {
      binding.borderCard.strokeColor = 0
      binding.root.alpha = 0.5F
    }
    binding.tvTitle.text = model.name

    binding.root.setOnClickListener {
      listener?.onItemClick(position, model, RecyclerViewActionType.BROWSE_ALL_POSTER_CAT_CLICKED.ordinal)
    }

    super.bind(position, item)
  }

}
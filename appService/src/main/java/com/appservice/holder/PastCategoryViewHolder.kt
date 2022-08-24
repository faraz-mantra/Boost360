package com.appservice.holder

import androidx.core.view.isVisible
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ListItemPastCategoryBinding
import com.appservice.model.updateBusiness.pastupdates.PastCategoriesModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class PastCategoryViewHolder(binding: ListItemPastCategoryBinding) :
    AppBaseRecyclerViewHolder<ListItemPastCategoryBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val categoryItem = item as PastCategoriesModel
        binding.tvCategoryName.text = "${categoryItem.categoryTitle} (${categoryItem.categoryCount})"

        binding.root.setOnClickListener {
            listener?.onItemClick(position, categoryItem, RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal)
        }

        binding.vwSelectIndicator.isVisible = categoryItem.isSelected

        super.bind(position, item)
    }
}
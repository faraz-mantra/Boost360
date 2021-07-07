package com.appservice.holder

import com.appservice.databinding.ItemCreateCategoryBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.common.Category


class CreateCategoryViewHolder(binding: ItemCreateCategoryBinding) :
  AppBaseRecyclerViewHolder<ItemCreateCategoryBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val category = item as Category
    binding.crbCategory.text = category.name
    binding.ctvProductCount.text = "${category.countItems} products under this category"

  }
}

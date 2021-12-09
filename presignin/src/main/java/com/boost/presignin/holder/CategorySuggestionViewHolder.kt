package com.boost.presignin.holder

import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ListItemCategorySuggestionBinding
import com.boost.presignin.model.category.ApiCategoryResponseCategory
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.utils.makeSectionOfTextBold

class CategorySuggestionViewHolder(binding: ListItemCategorySuggestionBinding) : AppBaseRecyclerViewHolder<ListItemCategorySuggestionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ApiCategoryResponseCategory ?: return
    binding.tvSubCat.text = "in " + data.subCategory
    binding.tvCat.text = makeSectionOfTextBold(data.name ?: "", data.searchKeyword ?: "",font = R.font.inter_medium)
    binding.root.setOnClickListener { onItemClick(position, item) }
  }

  private fun onItemClick(position: Int, item: BaseRecyclerViewItem) {
    listener?.onItemClick(position, item, RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal)
  }
}
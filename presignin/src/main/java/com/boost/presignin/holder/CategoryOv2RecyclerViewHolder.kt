package com.boost.presignin.holder

import android.view.View
import androidx.core.view.isInvisible
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ItemWebsiteCategoriesBinding
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem

class CategoryOv2RecyclerViewHolder constructor(binding: ItemWebsiteCategoriesBinding) : AppBaseRecyclerViewHolder<ItemWebsiteCategoriesBinding>(binding) {

  private var model: CategoryDataModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? CategoryDataModel
    setViews(model)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.layoutSuggestDomainSelect, binding.radioAsBusinessWebsite -> onCardClicked()
    }
  }

  private fun onCardClicked() {
    model?.isSelected = model?.isSelected != true && binding.radioAsBusinessWebsite.isChecked != true
    listener?.onItemClick(position = adapterPosition, item = model, actionType = RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal)
  }

  private fun setCardSelection(isSelected: Boolean) {
    binding.radioAsBusinessWebsite.isChecked = isSelected
    binding.ivBar.isInvisible = isSelected.not()

  }

  private fun setViews(model: CategoryDataModel?) {
    val activity = this.activity ?: return
    binding.tvCategoryTitle.text = if (model?.textChangeRTLAndSVC == false) model.getCategoryWithoutNewLine() else model?.getCategoryName()
    binding.tvCategoryExamples.text = model?.getSectionsTitles()
    val drawable = model?.getImage(activity) ?: return
    binding.ivCatImg.setImageDrawable(drawable)
    binding.ivCatImg.setTintColor(getColor(R.color.black_4a4a4a)!!)
    setClickListeners(binding.layoutSuggestDomainSelect, binding.radioAsBusinessWebsite)
    setCardSelection(model.isSelected)
  }

}
package com.boost.presignin.holder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ItemWebsiteCategoriesBinding
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem

class CategoryOv2RecyclerViewHolder constructor(binding: ItemWebsiteCategoriesBinding) :
  AppBaseRecyclerViewHolder<ItemWebsiteCategoriesBinding>(binding){

  private var model: CategoryDataModelOv2? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? CategoryDataModelOv2
    setViews(model)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.layoutSuggestDomainSelect,binding.radioAsBusinessWebsite -> onCardClicked()
    }
  }

  private fun onCardClicked() {
    model?.isSelected = model?.isSelected != true && binding.radioAsBusinessWebsite.isChecked!=true
    listener?.onItemClick(
      position = adapterPosition,
      item = model,
      actionType = RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal
    )
  }

  private fun setCardSelection(isSelected: Boolean) {
    if (isSelected) {
      binding.radioAsBusinessWebsite.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4a4a4a"))
    } else {
      binding.radioAsBusinessWebsite.buttonTintList = ColorStateList.valueOf(Color.parseColor("#bbbbbb"))
    }
    binding.radioAsBusinessWebsite.isChecked =isSelected

  }


  private fun setViews(model: CategoryDataModelOv2?) {
    val resources = getResources() ?: return
    val activity = this.activity ?: return
    binding.tvCategoryTitle.text = model?.category_Name
    val drawable = model?.getImage(activity) ?: return
    binding.ivCatImg.setImageDrawable(drawable)
    binding.ivCatImg.setTintColor(getColor(R.color.black_4a4a4a)!!)
    setClickListeners(binding.layoutSuggestDomainSelect)
    setCardSelection(model.isSelected)

  }



}
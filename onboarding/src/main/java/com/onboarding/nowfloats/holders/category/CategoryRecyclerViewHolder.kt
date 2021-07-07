package com.onboarding.nowfloats.holders.category

import android.view.View
import com.framework.extensions.invisible
import com.framework.extensions.setTextColorCompat
import com.framework.extensions.visible
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.ItemCategoryBinding
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class CategoryRecyclerViewHolder constructor(binding: ItemCategoryBinding) :
  AppBaseRecyclerViewHolder<ItemCategoryBinding>(binding) {

  private var model: CategoryDataModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? CategoryDataModel
    setViews(model)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.card -> onCardClicked()
    }
  }

  private fun onCardClicked() {
    model?.isSelected = model?.isSelected?.not() ?: true
    listener?.onItemClick(
      position = adapterPosition,
      item = model,
      actionType = RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal
    )
  }

  private fun setCardSelection(isSelected: Boolean) {
    if (isSelected) {
      binding.cardBg.setBackgroundResource(R.drawable.bg_card_blue)
      getColor(R.color.white)?.let { binding.name.setTextColorCompat(it) }
      getColor(R.color.white)?.let { binding.image.setTintColor(it) }
      binding.check.visible()
    } else {
      binding.cardBg.background = null
      getColor(R.color.white)?.let { binding.cardBg.setBackgroundColor(it) }
      getColor(R.color.dodger_blue_two)?.let { binding.name.setTextColorCompat(it) }
      getColor(R.color.dodger_blue_two)?.let { binding.image.setTintColor(it) }
      binding.check.invisible()
    }
  }

  private fun setViews(model: CategoryDataModel?) {
    val resources = getResources() ?: return
    val activity = this.activity ?: return
    binding.name.text = model?.category_Name
    val drawable = model?.getImage(activity) ?: return
    binding.image.setImageDrawable(drawable)
    setClickListeners(binding.card)
    setCardSelection(model.isSelected)
  }


}
package com.boost.presignin.holders

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ItemCategoryLayoutBinding
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.setTextColorCompat
import com.framework.extensions.visible

class CategoryRecyclerViewHolder constructor(binding: ItemCategoryLayoutBinding) :
  AppBaseRecyclerViewHolder<ItemCategoryLayoutBinding>(binding){

  private var model: CategoryDataModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? CategoryDataModel
    setViews(model)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.card,binding.check -> onCardClicked()
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
      getColor(R.color.view_background_1)?.let { binding.cardBg.setBackgroundColor(it) }
      binding.check.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4a4a4a"))
    } else {
      binding.cardBg.background = null
      getColor(R.color.white)?.let { binding.cardBg.setBackgroundColor(it) }
      binding.check.buttonTintList = ColorStateList.valueOf(Color.parseColor("#bbbbbb"))
    }
    binding.check.isChecked =isSelected

  }


  private fun setViews(model: CategoryDataModel?) {
    val resources = getResources() ?: return
    val activity = this.activity ?: return
    binding.name.text = model?.category_Name
    val drawable = model?.getImage(activity) ?: return
    binding.image.setImageDrawable(drawable)
    binding.image.setTintColor(getColor(R.color.black_4a4a4a)!!)
    binding.categoryImage.setImageDrawable(model.getCategoryImage(activity))
    setClickListeners(binding.card)
    setCardSelection(model.isSelected)

  }



}
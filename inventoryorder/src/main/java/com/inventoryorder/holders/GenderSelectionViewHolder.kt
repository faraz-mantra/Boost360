package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetSelectGenderBinding
import com.inventoryorder.model.bottomsheet.GenderSelectionModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class GenderSelectionViewHolder(binding: ItemBottomSheetSelectGenderBinding) :
  AppBaseRecyclerViewHolder<ItemBottomSheetSelectGenderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as GenderSelectionModel
    setData(data)
  }

  private fun setData(model: GenderSelectionModel) {
    model.getIcon()?.let { binding.ivGenderSelected.setImageResource(it) }
    binding.tvSelectGender.text = model.genderType
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.GENDER_SELECT_ITEM.ordinal
      )
    }

  }

}
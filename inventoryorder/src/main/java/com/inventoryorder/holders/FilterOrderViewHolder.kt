package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetFilterBinding
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class FilterOrderViewHolder(binding: ItemBottomSheetFilterBinding) :
  AppBaseRecyclerViewHolder<ItemBottomSheetFilterBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as FilterModel
    setDataForServiceLocations(data)
  }

  private fun setDataForServiceLocations(model: FilterModel) {
    model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
    binding.tvOptionSelected.text = model.type
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.FILTER_ACTION_ITEM.ordinal
      )
    }
  }

}
package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetFilterBinding
import com.inventoryorder.databinding.ItemBottomTimeSlotBinding
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.model.timeSlot.TimeSlotData
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class TimeSlotViewHolder(binding: ItemBottomTimeSlotBinding) :
  AppBaseRecyclerViewHolder<ItemBottomTimeSlotBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as TimeSlotData
    setDataForServiceLocations(data)
  }

  private fun setDataForServiceLocations(model: TimeSlotData) {
    model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
    binding.tvOptionSelected.text = model.getTimeSlotText()
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.TIME_SLOT_ACTION_ITEM.ordinal
      )
    }
  }

}
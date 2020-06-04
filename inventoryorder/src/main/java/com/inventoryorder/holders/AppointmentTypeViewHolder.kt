package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetAppointmentTypeBinding
import com.inventoryorder.model.bottomsheet.AppointMentTypeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AppointmentTypeViewHolder(binding: ItemBottomSheetAppointmentTypeBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetAppointmentTypeBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as AppointMentTypeModel
    setData(data)
  }

  private fun setData(model: AppointMentTypeModel) {
    model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
    binding.tvOptionSelected.text = model.appointmentTypeSelectedName
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.APPOINTMENT_ITEM_CLICKED.ordinal)
    }
  }

}
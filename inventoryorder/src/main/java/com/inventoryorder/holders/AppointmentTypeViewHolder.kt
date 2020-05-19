package com.inventoryorder.holders

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetAppointmentTypeBinding
import com.inventoryorder.model.bottomsheet.AppointMentTypeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AppointmentTypeViewHolder(binding : ItemBottomSheetAppointmentTypeBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetAppointmentTypeBinding>(binding){

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as AppointMentTypeModel
        setData(data)
    }

    private fun setData(data: AppointMentTypeModel) {
        binding.ivOptionSelected.setImageResource(R.drawable.ic_option_unselected)
        binding.tvOptionSelected.text = data.appointmentTypeSelectedName
        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.APPOINTMENT_ITEM_CLICKED.ordinal)
        }
    }

}
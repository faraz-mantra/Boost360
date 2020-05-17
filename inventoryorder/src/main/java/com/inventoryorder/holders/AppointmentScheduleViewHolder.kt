package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemAppointmentScheduleBinding
import com.inventoryorder.model.AppointmentScheduleModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AppointmentScheduleViewHolder(binding: ItemAppointmentScheduleBinding) : AppBaseRecyclerViewHolder<ItemAppointmentScheduleBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

       val data =  item as AppointmentScheduleModel
       setData(data)
    }

    fun setData(model: AppointmentScheduleModel) {

        binding.tvAppointmentTime.text = model.appointMentSchedule

    }


}
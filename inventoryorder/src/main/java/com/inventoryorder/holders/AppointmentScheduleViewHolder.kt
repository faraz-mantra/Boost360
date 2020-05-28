package com.inventoryorder.holders

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemAppointmentScheduleBinding
import com.inventoryorder.model.AppointmentScheduleModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AppointmentScheduleViewHolder(binding: ItemAppointmentScheduleBinding) : AppBaseRecyclerViewHolder<ItemAppointmentScheduleBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as AppointmentScheduleModel
        setData(data)
    }

    fun setData(model: AppointmentScheduleModel) {
        binding.tvAppointmentTime.text = model.appointMeantSchedule
        binding.mainView.background = activity?.let { AppCompatResources.getDrawable(it,model.setSelectedItemBackground()) }
        binding.tvAppointmentTime.setTextColor( model.setSelectedItemColor())

        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.APPOINTMENT_SCHEDULE.ordinal)
        }

    }
}
//binding.tvAppointmentTime.setTextColor(activity?.let { ContextCompat.getColor(it, model.setSelectedItemColor()) }!!)

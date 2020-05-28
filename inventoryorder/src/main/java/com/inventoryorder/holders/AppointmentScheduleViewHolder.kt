package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.R
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
        activity?.let {
            if (adapterPosition == 0) {
                binding.mainView.setBackgroundColor(ContextCompat.getColor(it, R.color.light_grey))
                binding.tvAppointmentTime.setTextColor(ContextCompat.getColor(it, R.color.warm_grey_10))
            } else binding.tvAppointmentTime.setTextColor(ContextCompat.getColor(it, R.color.primary_grey))

            binding.tvAppointmentTime.text = model.appointMeantSchedule
            binding.mainView.setOnClickListener {
                listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.APPOINTMENT_SCHEDULE.ordinal)
            }

        }
    }

}
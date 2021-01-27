package com.appservice.holder

import android.view.View
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.common.AppointmentModel

class WeeklyAppointmentViewHolder(binding: RecyclerItemSessionBinding) : AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        setIsRecyclable(false)
        val data = item as AppointmentModel
        when (data.isAppliedOnAllDaysViewVisible && data.isTurnedOn) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
            true -> binding.layoutSessionCreate.visibility = View.VISIBLE
        }
        binding.ctvTitleDay.text = "${data.day}"
        binding.toggleOnOff.isOn = data.isTurnedOn
        binding.toggleOnOff.setOnToggledListener { _, isOn ->
//            data.isAppliedOnAllDaysViewVisible = isOn
//            data.isTurnedOn = isOn

            listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
            data.changeDayTurned(isOn)
        }
        binding.ccbAllDay.isChecked = data.isAppliedOnAllDays
        binding.ccbAllDay.setOnCheckedChangeListener { _, isChecked ->
//            data.isAppliedOnAllDays = isChecked
            listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)
            when (isChecked) {
                true -> {
                    data.applyOnAllDays(binding.spinnerStartTiming.selectedItem as String, binding.spinnerEndTiming.selectedItem as String)
                }
                else -> {
                    data.removeApplyOnAllDays(data)
                }
            }
        }
        binding.ctvAddSession.setOnClickListener {
            data.fromTiming = binding.spinnerStartTiming.selectedItem as String
            data.toTiming = binding.spinnerEndTiming.selectedItem as String
            data.addSession(data)
            listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)

        }

    }


}




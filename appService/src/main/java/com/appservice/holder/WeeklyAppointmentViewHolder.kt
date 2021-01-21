package com.appservice.holder

import android.view.View
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.common.AppointmentModel

class WeeklyAppointmentViewHolder(binding: RecyclerItemSessionBinding) : AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {
    private lateinit var data: AppointmentModel

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        this.data = item as AppointmentModel
        when (data.isAppliedOnAllDaysViewVisible && data.isTurnedOn) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
            true -> binding.layoutSessionCreate.visibility = View.VISIBLE
        }
        binding.ccvTitleDay.text = "${data.day}"
        binding.toggleOnOff.isOn = data.isTurnedOn
        binding.toggleOnOff.setOnToggledListener { _, isOn ->
            data.isAppliedOnAllDaysViewVisible = isOn
            data.isTurnedOn = isOn
            listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
        }
        binding.ccbAllDay.isChecked = data.isAppliedOnAllDays
        binding.ccbAllDay.setOnCheckedChangeListener { _, isChecked ->
            data.isAppliedOnAllDays = isChecked
            listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)
        }
        binding.ctvAddSession.setOnClickListener {
            data.fromTiming = binding.spinnerStartTiming.selectedItem as String
            data.toTiming = binding.spinnerEndTiming.selectedItem as String
            listener?.onItemClick(position, data, RecyclerViewActionType.ADD_SESSION.ordinal)
        }

    }


}




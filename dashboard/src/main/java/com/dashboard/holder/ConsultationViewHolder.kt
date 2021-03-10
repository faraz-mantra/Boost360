package com.dashboard.holder

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.ownerinfo.ConsultationHoursModel
import com.dashboard.controller.ui.ownerinfo.TimeSlot
import com.dashboard.databinding.RecyclerItemConsultationBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.BaseApplication

class ConsultationViewHolder(binding: RecyclerItemConsultationBinding) : AppBaseRecyclerViewHolder<RecyclerItemConsultationBinding>(binding) {
    val businessHours: Array<String> = BaseApplication.instance.applicationContext.resources.getStringArray(R.array.business_hours_arrays)
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        setIsRecyclable(false)

        val data = item as ConsultationHoursModel


        binding.toggleOnOff.isEnabled = data.isViewEnabled ?: true
        when (data.isTurnedOn) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
            true -> binding.layoutSessionCreate.visibility = View.VISIBLE
        }
        when (data.isDataAppliedOnMyDay!! && data.isTurnedOn!!) {
            true -> binding.layoutSessionCreate.visibility = View.GONE
        }
        when (data.isDataAppliedOnMyDay) {
            true -> data.removeApplyOnAllDays(data = data)
        }
        binding.ctvTitleDay.text = "${data.day}"

        binding.toggleOnOff.isOn = data.isTurnedOn!!
        binding.toggleOnOff.setOnToggledListener { _, isOn ->
            data.changeDayTurned(isOn)
            listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
        }
        binding.ccbAllDay.isChecked = data.isAppliedOnAllDays!!
        binding.ccbAllDay.setOnCheckedChangeListener { _, isChecked ->
            data.isAppliedOnAllDays = isChecked
            listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)
        }
        binding.ctvAddSession.setOnClickListener {
            data.addSession()
            listener?.onItemClick(position, data, RecyclerViewActionType.ADD_SESSION.ordinal)
        }

        when (binding.layoutSessionCreate.visibility == View.VISIBLE) {
            true -> addTimeSlots(data)
            else -> binding.llTimeSlot.removeAllViewsInLayout()
        }

    }


    private fun addTimeSlots(data: ConsultationHoursModel) {
        if (data.timeSlots.isNullOrEmpty()) {
            binding.llTimeSlot.removeAllViewsInLayout()
        } else {
            for (item in data.timeSlots) {
                binding.llTimeSlot.addView(getTimeSlotView(item))
            }
        }
    }

    private fun getTimeSlotView(timeSlot: TimeSlot): View {
        val itemView = LayoutInflater.from(binding.llTimeSlot.context).inflate(R.layout.item_time_slot, null, false)
        val fromSpinner = itemView.findViewById(R.id.spinner_start_timing) as AppCompatSpinner
        val toSpinner = itemView.findViewById(R.id.spinner_end_timing) as AppCompatSpinner
        sessionTimingHandler(fromSpinner, timeSlot, toSpinner)
        fromSpinner.setSelection(businessHours.indexOf(element = timeSlot.from))
        toSpinner.setSelection(businessHours.indexOf(element = timeSlot.to))
        return itemView
    }

    private fun sessionTimingHandler(fromSpinner: AppCompatSpinner, timeSlot: TimeSlot, toSpinner: AppCompatSpinner) {
        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                timeSlot.from = businessHours[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                timeSlot.to = businessHours[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }


}




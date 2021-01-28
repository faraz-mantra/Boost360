package com.appservice.holder

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.common.TimeSlot

class WeeklyAppointmentViewHolder(binding: RecyclerItemSessionBinding) : AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        setIsRecyclable(false)
        val data = item as AppointmentModel

        // turned on or off visibility of checkbox for all days
//        binding.ccbAllDay.visibility = data.isAppliedOnAllDaysViewVisible!!? Visi:

        when (data.isTurnedOn) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
            true -> binding.layoutSessionCreate.visibility = View.VISIBLE
        }
        binding.ctvTitleDay.text = "${data.day}"
        binding.toggleOnOff.isOn = data.isTurnedOn
        binding.toggleOnOff.setOnToggledListener { _, isOn ->
            data.changeDayTurned(isOn)
            listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
        }
        binding.ccbAllDay.isChecked = data.isAppliedOnAllDays
        binding.ccbAllDay.setOnCheckedChangeListener { _, isChecked ->
            data.isAppliedOnAllDays = isChecked
            listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)
        }
        binding.ctvAddSession.setOnClickListener {
            data.addSession()
            listener?.onItemClick(position, data, RecyclerViewActionType.ADD_SESSION.ordinal)
        }

        addTimeSlots(data);
    }

    fun addTimeSlots(data: AppointmentModel){
        if(data.timeSlots.isNullOrEmpty()){
            binding.llTimeSlot.removeAllViewsInLayout();
        }else{
            for (item in data.timeSlots!!){
                binding.llTimeSlot.addView(getTimeSlotView(item));
            }
        }
    }

    fun getTimeSlotView(timeSlot: TimeSlot): View{
        val itemView = LayoutInflater.from(binding.llTimeSlot.context).inflate(R.layout.item_time_slot, null, false);
        val fromSpinner = itemView.findViewById<AppCompatSpinner>(R.id.spinner_start_timing)
        val toSpinner = itemView.findViewById<AppCompatSpinner>(R.id.spinner_end_timing);
        return itemView;
    }


}




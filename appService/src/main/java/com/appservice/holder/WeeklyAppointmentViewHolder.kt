package com.appservice.holder

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.common.TimeSlot
import com.framework.BaseApplication

class WeeklyAppointmentViewHolder(binding: RecyclerItemSessionBinding) :
  AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {
  val businessHours: Array<String> =
    BaseApplication.instance.applicationContext.resources.getStringArray(R.array.business_hours_arrays)

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    setIsRecyclable(false)

    val data = item as? AppointmentModel ?: return

    // turned on or off visibility of checkbox for all days
//        binding.ccbAllDay.visibility = data.isAppliedOnAllDaysViewVisible
    //is data applied on  true -> add session visibility gone
    //isdataAPpliedonmy day true -> make all views unresponsive -- disable modification
    //

//        binding.toggleOnOff.isEnabled = data.isTurnedOn ?: true
    when (data.isTurnedOn) {
      false -> {
        binding.layoutSessionCreate.visibility = View.GONE
//                binding.rlRoot.background = null
      }
      true -> {
        binding.layoutSessionCreate.visibility = View.VISIBLE
//                binding.rlRoot.setBackgroundResource(R.drawable.rounded_stroke_grey)
      }

    }
//        when (data.isDataAppliedOnMyDay!! && data.isTurnedOn!!) {
//            true -> binding.layoutSessionCreate.visibility = View.GONE
//        }
//        when (data.isDataAppliedOnMyDay) {
//            true -> data.removeApplyOnAllDays(data = data)
//        }
    binding.ctvTitleDay.text = "${data.day}"

    binding.toggleOnOff.isOn = data.isTurnedOn!!
    binding.toggleOnOff.setOnToggledListener { _, isOn ->
      data.changeDayTurned(isOn)
      listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
    }
    applyOnAllDaysLogic(data)

    binding.ctvAddSession.setOnClickListener {
      data.addSession()
      listener?.onItemClick(position, data, RecyclerViewActionType.ADD_SESSION.ordinal)
    }
//if binding.layoutsessioncreate.visbility ==visible then add time slot will work else {
    //  binding.llTimeSlot.removeAllViewsInLayout(); }
    when (binding.layoutSessionCreate.visibility == View.VISIBLE) {
      true -> addTimeSlots(data)
      else -> binding.llTimeSlot.removeAllViewsInLayout()
    }

  }

  fun applyOnAllDaysLogic(data: AppointmentModel) {
    binding.ccbAllDay.isChecked = data.isAppliedOnAllDays!!
    binding.ccbAllDay.setOnCheckedChangeListener { _, isChecked ->
      data.isAppliedOnAllDays = isChecked
      listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal)
    }
    when (data.isAppliedOnAllDaysViewVisible) {
      false -> {
        binding.ccbAllDay.visibility = View.GONE
      }
      true -> {
        binding.ccbAllDay.visibility = View.VISIBLE
      }
    }
  }


  fun addTimeSlots(data: AppointmentModel) {
    if (data.timeSlots.isNullOrEmpty()) {
      binding.llTimeSlot.removeAllViewsInLayout()
    } else {
      for (item in data.timeSlots) {
        binding.llTimeSlot.addView(getTimeSlotView(item))
      }
    }
  }

  private fun getTimeSlotView(timeSlot: TimeSlot): View {
    val itemView = LayoutInflater.from(binding.llTimeSlot.context).inflate(R.layout.item_ts_staff, null, false);
    val fromSpinner = itemView.findViewById(R.id.spinner_start_timing) as AppCompatSpinner
    val toSpinner = itemView.findViewById(R.id.spinner_end_timing) as AppCompatSpinner
    sessionTimingHandler(fromSpinner, timeSlot, toSpinner)
    fromSpinner.setSelection(businessHours.indexOf(element =timeSlot.from))
    toSpinner.setSelection(businessHours.indexOf(element= timeSlot.to))
    return itemView
  }

  private fun sessionTimingHandler(
    fromSpinner: AppCompatSpinner,
    timeSlot: TimeSlot,
    toSpinner: AppCompatSpinner
  ) {
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




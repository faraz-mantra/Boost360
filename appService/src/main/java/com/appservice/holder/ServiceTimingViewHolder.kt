package com.appservice.holder

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemServiceTimingBinding
import com.appservice.model.serviceTiming.ServiceTiming
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.FORMAT_HH_MMA
import com.framework.utils.DateUtils.FORMAT_HH_MM_A
import com.framework.utils.DateUtils.isBetweenValidTime
import com.framework.utils.DateUtils.parseDate

class ServiceTimingViewHolder(binding: ItemServiceTimingBinding) :
  AppBaseRecyclerViewHolder<ItemServiceTimingBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val data = item as? ServiceTiming ?: return
    var businessHours = arrayOf(
      "12:00 AM",
      "12:30 AM",
      "01:00 AM",
      "01:30 AM",
      "02:00 AM",
      "02:30 AM",
      "03:00 AM",
      "03:30 AM",
      "04:00 AM",
      "04:30 AM",
      "05:00 AM",
      "05:30 AM",
      "06:00 AM",
      "06:30 AM",
      "07:00 AM",
      "07:30 AM",
      "08:00 AM",
      "08:30 AM",
      "09:00 AM",
      "09:30 AM",
      "10:00 AM",
      "10:30 AM",
      "11:00 AM",
      "11:30 AM",
      "12:00 PM",
      "12:30 PM",
      "01:00 PM",
      "01:30 PM",
      "02:00 PM",
      "02:30 PM",
      "03:00 PM",
      "03:30 PM",
      "04:00 PM",
      "04:30 PM",
      "05:00 PM",
      "05:30 PM",
      "06:00 PM",
      "06:30 PM",
      "07:00 PM",
      "07:30 PM",
      "08:00 PM",
      "08:30 PM",
      "09:00 PM",
      "09:30 PM",
      "10:00 PM",
      "10:30 PM",
      "11:00 PM",
      "11:30 PM",
    )
    binding.ctvTitleDay.text = "${data.day}"
    if (data.isOpenDay()) {
      binding.toggleOnOff.visible()
      binding.ctvCloseDay.gone()
    } else {
      data.isToggle = false
      binding.toggleOnOff.gone()
      binding.ctvCloseDay.visible()
    }
    binding.toggleOnOff.isOn = data.isToggle
    binding.toggleOnOff.setOnToggledListener { _, isOn ->
      data.isToggle = isOn
      listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
    }
    val isShowAppliedAll =
      (data.appliedOnPosition != null && position == data.appliedOnPosition && data.isToggle)
    val isShowSpinnerView = (data.appliedOnPosition == null && data.isToggle) || isShowAppliedAll
    binding.mainView.setBackgroundResource(if (isShowSpinnerView) R.drawable.rounded_stroke_grey else 0)
    binding.layoutTimingView.visibility = if (isShowSpinnerView) View.VISIBLE else View.GONE
    binding.ccbAllDay.isChecked = isShowAppliedAll

    binding.ccbAllDay.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal
      )
    }

    if (data.isOpenDay()) {
      var startDate = data.businessTiming?.startTime?.parseDate(FORMAT_HH_MMA)
      if (startDate == null) startDate = data.businessTiming?.startTime?.parseDate(FORMAT_HH_MM_A)
      var endDate = data.businessTiming?.endTime?.parseDate(FORMAT_HH_MMA)
      if (endDate == null) endDate = data.businessTiming?.endTime?.parseDate(FORMAT_HH_MM_A)

      if (startDate != null && endDate != null) {
        businessHours = businessHours.filter {
          val dateN = it.parseDate(FORMAT_HH_MM_A)
          (dateN != null && isBetweenValidTime(startDate, endDate, dateN))
        }.toTypedArray()
      }
    }

    if (isShowSpinnerView) {
      setArrayAdapter(binding.spinnerStartTiming, businessHours)
      setArrayAdapter(binding.spinnerEndTiming, businessHours)
      binding.spinnerStartTiming.setSelection(businessHours.indexOf(element = data.getTimeData().from))
      binding.spinnerEndTiming.setSelection(businessHours.indexOf(element = data.getTimeData().to))
      binding.spinnerStartTiming.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            data.getTimeData().from = businessHours[p2]
          }

          override fun onNothingSelected(p0: AdapterView<*>?) {
          }

        }
      binding.spinnerEndTiming.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            data.getTimeData().to = businessHours[p2]
          }

          override fun onNothingSelected(p0: AdapterView<*>?) {
          }
        }
    }
  }

  private fun setArrayAdapter(spinner: AppCompatSpinner, businessHours: Array<String>) {
    val adapter1 =
      ArrayAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, businessHours)
    spinner.adapter = adapter1
  }
}




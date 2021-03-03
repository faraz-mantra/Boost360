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
import com.framework.BaseApplication
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.FORMAT_HH_MMA
import com.framework.utils.DateUtils.isBetweenValidTime
import com.framework.utils.DateUtils.parseDate

class ServiceTimingViewHolder(binding: ItemServiceTimingBinding) : AppBaseRecyclerViewHolder<ItemServiceTimingBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val data = item as? ServiceTiming ?: return
    var businessHours: Array<String> = BaseApplication.instance.applicationContext.resources.getStringArray(R.array.business_hours_arrays)
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
    val isShowAppliedAll = (data.appliedOnPosition != null && position == data.appliedOnPosition && data.isToggle)
    val isShowSpinnerView = (data.appliedOnPosition == null && data.isToggle) || isShowAppliedAll
    binding.mainView.setBackgroundResource(if (isShowSpinnerView) R.drawable.rounded_stroke_grey else 0)
    binding.layoutTimingView.visibility = if (isShowSpinnerView) View.VISIBLE else View.GONE
    binding.ccbAllDay.isChecked = isShowAppliedAll

    binding.ccbAllDay.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal) }

    if (data.isOpenDay()) {
      val startDate = data.businessTiming?.startTime?.parseDate(FORMAT_HH_MMA)
      val endDate = data.businessTiming?.endTime?.parseDate(FORMAT_HH_MMA)
      if (startDate != null && endDate != null) {
        businessHours = businessHours.filter {
          val dateN = it.parseDate(FORMAT_HH_MMA)
          (dateN != null && isBetweenValidTime(startDate, endDate, dateN))
        }.toTypedArray()
      }
    }

    if (isShowSpinnerView) {
      setArrayAdapter(binding.spinnerStartTiming, businessHours)
      setArrayAdapter(binding.spinnerEndTiming, businessHours)
      binding.spinnerStartTiming.setSelection(businessHours.indexOf(element = data.getTimeData().from))
      binding.spinnerEndTiming.setSelection(businessHours.indexOf(element = data.getTimeData().to))
      binding.spinnerStartTiming.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          data.getTimeData().from = businessHours[p2]
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

      }
      binding.spinnerEndTiming.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          data.getTimeData().to = businessHours[p2]
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
      }
    }
  }

  private fun setArrayAdapter(spinner: AppCompatSpinner, businessHours: Array<String>) {
    val adapter1 = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, businessHours)
    spinner.adapter = adapter1
  }
}




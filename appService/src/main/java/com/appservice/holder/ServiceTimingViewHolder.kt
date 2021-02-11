package com.appservice.holder

import android.view.View
import android.widget.AdapterView
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemServiceTimingBinding
import com.appservice.model.serviceTiming.ServiceTiming
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.BaseApplication

class ServiceTimingViewHolder(binding: ItemServiceTimingBinding) : AppBaseRecyclerViewHolder<ItemServiceTimingBinding>(binding) {

  val businessHours: Array<String> = BaseApplication.instance.applicationContext.resources.getStringArray(R.array.business_hours_arrays)

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val data = item as? ServiceTiming ?: return
    binding.ctvTitleDay.text = "${data.day}"
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

    if (isShowSpinnerView) {
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
}




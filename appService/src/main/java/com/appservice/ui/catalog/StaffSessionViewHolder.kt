package com.appservice.ui.catalog

import android.view.View
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.StaffTimingModel

class StaffSessionViewHolder(binding: RecyclerItemSessionBinding) : AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {
    private lateinit var data: StaffTimingModel

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        setIsRecyclable(false)
        this.data = item as StaffTimingModel
        when (data.isTurnedOn) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
            true -> binding.layoutSessionCreate.visibility = View.VISIBLE
        }
        binding.ccvTitleDay.text = "${data.day}"
        binding.toggleOnOff.isOn = data.isTurnedOn
        binding.toggleOnOff.setOnToggledListener { _, isOn ->
            data.isTurnedOn = isOn
            listener?.onItemClick(position, data, RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal)
        }
        binding.layoutSessionCreate?.ccbAllDay.isChecked = data.isAppliedOnAllDays

    }
}




package com.appservice.staffs.recycler

import android.view.View
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemSessionBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.StaffTimingModel

class StaffSessionViewHolder(binding: RecyclerItemSessionBinding) : AppBaseRecyclerViewHolder<RecyclerItemSessionBinding>(binding) {
    private lateinit var data: StaffTimingModel

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        this.data = item as StaffTimingModel
        when (data.isAppliedOnAllDays) {
            false -> binding.layoutSessionCreate.visibility = View.GONE
        }
        binding.ccvTitleDay.text = "${data.day}"
        binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SESSION_ITEM_CLICK.ordinal) }
        setClickListeners(binding.toggleOnOff)
        binding.toggleOnOff.setOnToggledListener { toggleableView, isOn ->
            when (isOn) {
                true -> {

                }
            }

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.toggleOnOff -> {
//                Toast.makeText(getApplicationContext(), data.day, Toast.LENGTH_SHORT).show()
                when (data.isAppliedOnAllDays) {
                    true -> {
                        data.isAppliedOnAllDays = false
                    }
                }
            }
        }
    }
}
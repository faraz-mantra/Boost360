package com.appservice.staffs.recycler

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.DataItem

class StaffListingViewHolder(binding: RecyclerItemStaffListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemStaffListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as DataItem
        binding.ctvName.text = "${data.name}"
        binding.flViewProfile.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.STAFF_LISTING_CLICK.ordinal) }
    }

}

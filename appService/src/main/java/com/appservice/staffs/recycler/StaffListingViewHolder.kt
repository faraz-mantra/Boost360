package com.appservice.staffs.recycler

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.DataItem

class StaffListingViewHolder(binding: RecyclerItemStaffListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemStaffListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as DataItem
        binding.ctvName.text = "${data.name}"
        if (position == 0)
            binding.civImage.setImageResource(R.drawable.dummy_staff_img)
        binding.ctvLeaves.text = "Leaves"
        binding.ctvSpecialization.text = "5 year expereience"
        binding.flViewProfile.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.STAFF_LISTING_CLICK.ordinal) }
    }

}

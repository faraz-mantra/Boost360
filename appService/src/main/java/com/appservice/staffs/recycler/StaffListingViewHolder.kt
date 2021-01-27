package com.appservice.staffs.recycler

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.DataItem
import com.framework.glide.util.glideLoad

class StaffListingViewHolder(binding: RecyclerItemStaffListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemStaffListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as DataItem
        binding.ctvName.text = "${data.name}"
        binding.civImage.let { activity?.glideLoad(it, data.image.toString(), R.drawable.placeholder_image) }
        val specialisationsItem: ArrayList<String> = ArrayList()
        data.specialisations?.forEach { specialisationsItem.add(it.value!!) }
        binding.ctvSpecialization.text = specialisationsItem.joinToString(separator = ",")
        binding.btnViewProfile.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.STAFF_LISTING_CLICK.ordinal) }
    }

}

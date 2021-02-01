package com.appservice.staffs.recycler

import com.appservice.databinding.RecyclerItemServiceTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.staffs.model.ServiceTimingModel

class StaffTimingServicesViewHolder(binding: RecyclerItemServiceTimingBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceTimingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as ServiceTimingModel
        binding.ctvTimingServices.text = data.name
    }
}
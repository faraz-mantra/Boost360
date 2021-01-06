package com.appservice.ui.staffs.recycler

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemServiceBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.staffs.ui.home.model.ServiceModel

class StaffServiceViewHolder(binding: RecyclerItemServiceBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as ServiceModel
        binding.ccbServices.text = "${data.serviceName}"
        binding.ccbServices.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) }
    }
}
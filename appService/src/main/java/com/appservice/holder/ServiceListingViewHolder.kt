package com.appservice.holder

import com.appservice.databinding.RecyclerItemServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.model.ServiceListingResponse

class ServiceListingViewHolder(binding: RecyclerItemServiceListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as ServiceListingResponse
    }
}
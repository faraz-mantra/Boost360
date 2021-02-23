package com.appservice.holder

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemServiceSelectOfferBinding
import com.appservice.offers.models.SelectServiceModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class SelectOfferListingViewHolder(binding: RecyclerItemServiceSelectOfferBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceSelectOfferBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as SelectServiceModel.DataItemOfferService
        binding.ccbServices.text = "${data.name}"
        binding.ccbServices.isChecked = data.isChecked ?: false
        binding.ccbServices.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) }
    }
}
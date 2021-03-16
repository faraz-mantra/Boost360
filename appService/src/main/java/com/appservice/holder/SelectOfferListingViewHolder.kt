package com.appservice.holder

import android.text.Html
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemServiceSelectOfferBinding
import com.appservice.offers.models.SelectServiceModel
import com.appservice.offers.models.SelectServiceModel.*
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class SelectOfferListingViewHolder(binding: RecyclerItemServiceSelectOfferBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceSelectOfferBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as DataItemOfferService
        binding.ccbServices.text = Html.fromHtml("<b>${data.name}</b> (₹${data.discountedPrice} for ${data.duration} min)")
        binding.ccbServices.isChecked = data.isChecked ?: false
        binding.ccbServices.setOnClickListener {data.isChecked=true;listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) }
    }
}
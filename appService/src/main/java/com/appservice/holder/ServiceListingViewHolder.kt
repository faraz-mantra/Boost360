package com.appservice.holder

import android.graphics.Paint
import com.appservice.databinding.RecyclerItemServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.model.ItemsItem
import com.framework.glide.util.glideLoad

class ServiceListingViewHolder(binding: RecyclerItemServiceListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as ItemsItem
        binding.labelName.text = data.name ?: ""
        binding.labelCategory.text = data.category ?: "No category"
        binding.ctvDuration.text = "${data.duration ?: 0}min"
        binding.labelBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.labelBasePrice.text = "${data.currency ?: ""} ${data.discountAmount}"
        binding.labelPrice.text = "${data.currency ?: ""} ${data.price}"
        apply { activity?.glideLoad(binding.cardThumbnail, data.tileImage) }

    }

}
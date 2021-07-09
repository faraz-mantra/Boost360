package com.appservice.holder

import android.graphics.Paint
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.model.ItemsItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad

class ServiceListingViewHolder(binding: RecyclerItemServiceListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemServiceListingBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? ItemsItem) ?: return
    binding.labelName.text = data.name
    binding.labelCategory.text = data.getCategoryValue()
    binding.ctvDuration.text = "${data.duration ?: 0}min"

    if (data.price ?: 0.0 <= data.discountedPrice ?: 0.0) binding.labelBasePrice.gone() else binding.labelBasePrice.visible()

    binding.labelPrice.text = "${data.currency ?: "INR"} ${data.discountedPrice}"

    binding.labelBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    binding.labelBasePrice.text = "${data.currency ?: "INR"} ${data.price}"

    apply { activity?.glideLoad(binding.cardThumbnail, data.tileImage, R.drawable.placeholder_image_n) }
    binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) }
    binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_DATA_SHARE_CLICK.ordinal) }
    binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.SERVICE_WHATS_APP_SHARE.ordinal) }
  }

}
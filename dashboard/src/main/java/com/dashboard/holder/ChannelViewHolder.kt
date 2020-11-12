package com.dashboard.holder

import com.dashboard.R
import com.dashboard.databinding.ItemChannelDBinding
import com.dashboard.model.ChannelData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getNameAlternate
import com.onboarding.nowfloats.model.channel.isGoogleSearch
import com.onboarding.nowfloats.model.channel.isWhatsAppChannel

class ChannelViewHolder(binding: ItemChannelDBinding) : AppBaseRecyclerViewHolder<ItemChannelDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? ChannelData)?.channelData ?: return
    binding.title.text = data.getNameAlternate()
    val isConnect = ((data.isWhatsAppChannel() && data.channelActionData != null) || data.channelActionData != null || data.isGoogleSearch())
    getColor(if (isConnect) R.color.black_4f4f4f else R.color.warm_grey_two)?.let { binding.title.setTextColor(it) }
    data.getDrawable(activity)?.let { binding.image.setImageDrawable(it) }
    binding.image.apply { if (!isConnect) makeGreyscale() else removeGreyscale() }
    binding.executePendingBindings()
  }
}

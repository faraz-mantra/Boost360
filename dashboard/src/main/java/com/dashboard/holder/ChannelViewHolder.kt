package com.dashboard.holder

import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemChannelDBinding
import com.dashboard.model.live.channel.ChannelData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.onboarding.nowfloats.model.channel.*

class ChannelViewHolder(binding: ItemChannelDBinding) : AppBaseRecyclerViewHolder<ItemChannelDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? ChannelData)?.channelData ?: return
    binding.title.text = data.getNameAlternate()
    val isConnect = ((data.isWhatsAppChannel() && data.channelActionData != null) || data.channelAccessToken != null || data.isGoogleSearch())
    getColor(if (isConnect) R.color.black_4f4f4f else R.color.warm_grey_two)?.let { binding.title.setTextColor(it) }
    (if (!isConnect) data.getDrawableInActiveNew(activity) else data.getDrawableActiveNew(activity))?.let { binding.image.setImageDrawable(it) }
    binding.tickStatusIcon.visible()
    if (isConnect && data.isGoogleSearch()) {
      binding.tickStatusIcon.gone()
    } else if (isConnect) binding.tickStatusIcon.setImageResource(R.drawable.ic_tick_ok_d)
    else binding.tickStatusIcon.setImageResource(R.drawable.ic_tick_d)

    binding.mainContent.setOnClickListener { if (isConnect) listener?.onItemClick(position, item, RecyclerViewActionType.CHANNEL_ITEM_CLICK.ordinal) }
    binding.executePendingBindings()
  }
}

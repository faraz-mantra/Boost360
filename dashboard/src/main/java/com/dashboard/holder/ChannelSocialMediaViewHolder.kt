package com.dashboard.holder

import com.dashboard.databinding.ItemSocialMediaBinding
import com.dashboard.model.live.channel.ChannelStatusData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class ChannelSocialMediaViewHolder(binding: ItemSocialMediaBinding) : AppBaseRecyclerViewHolder<ItemSocialMediaBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ChannelStatusData
  }
}

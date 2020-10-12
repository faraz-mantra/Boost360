package com.dashboard.holder

import com.dashboard.databinding.ItemChannelDBinding
import com.dashboard.model.ChannelData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class ChannelViewHolder(binding: ItemChannelDBinding) : AppBaseRecyclerViewHolder<ItemChannelDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ChannelData ?: return
    binding.title.text = data.title
    data.icon?.let { binding.image.setImageResource(it) }
  }
}

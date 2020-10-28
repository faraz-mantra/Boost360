package com.dashboard.holder

import com.dashboard.R
import com.dashboard.databinding.ItemChannelDBinding
import com.dashboard.model.ChannelData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class ChannelViewHolder(binding: ItemChannelDBinding) : AppBaseRecyclerViewHolder<ItemChannelDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ChannelData ?: return
    binding.title.text = data.title
    getColor(if (data.isConnect == true) R.color.black_4f4f4f else R.color.warm_grey_two)?.let { binding.title.setTextColor(it) }
    data.icon?.let { binding.image.setImageResource(it) }
    binding.image.apply { if (data.isConnect == false) makeGreyscale() else removeGreyscale() }
  }
}

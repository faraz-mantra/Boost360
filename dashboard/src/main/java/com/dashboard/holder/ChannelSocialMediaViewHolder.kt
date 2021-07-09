package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemSocialMediaBinding
import com.dashboard.model.live.channel.ChannelStatusData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class ChannelSocialMediaViewHolder(binding: ItemSocialMediaBinding) : AppBaseRecyclerViewHolder<ItemSocialMediaBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ChannelStatusData
    data?.getIconChannel()?.let { binding.channelIcon.setImageResource(it) }
    data?.getIconConnected()?.let { binding.connectIcon.setImageResource(it) }
    data?.getCardColor()?.let { getColor(it)?.let { it1 -> binding.cardView.setCardBackgroundColor(it1) } }
    if (data?.isConnected == true) {
      binding.activeView.visible()
      binding.inActiveView.gone()
      binding.txtActive.text = data.getActiveString()?.let { activity?.getString(it) } ?: ""
      binding.totalCount.text = "${data.insightsData?.pageViewsTotal ?: 0}"
    } else {
      binding.activeView.gone()
      binding.inActiveView.visible()
      binding.txtInactive.text = data?.getNotActiveString()?.let { activity?.getString(it) } ?: ""
    }
    binding.btnActivate.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.CHANNEL_ACTIVATE_CLICK.ordinal) }
  }
}

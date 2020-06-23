package com.onboarding.nowfloats.holders.channel

import android.view.View
import com.framework.extensions.underlineText
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.ItemChannelsDisconnectBinding
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getName
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelsDisconnectViewHolder constructor(binding: ItemChannelsDisconnectBinding) : AppBaseRecyclerViewHolder<ItemChannelsDisconnectBinding>(binding) {

  private var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    model?.let { setViews(it) }
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.card -> listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.CHANNEL_DISCONNECT_CLICKED.ordinal)
      binding.whysync -> listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.CHANNEL_DISCONNECT_WHY_CLICKED.ordinal)
    }
  }

  private fun setViews(model: ChannelModel) {
    setClickListeners(binding.card, binding.whysync)
    binding.title.text = model.getName()
    binding.whysync.text = "Why Sync on ${model.getName()}"
    binding.whysync.underlineText(0, binding.whysync.text.length - 1)
    setSelection(model)
  }

  private fun setSelection(model: ChannelModel) {
    binding.image.setImageDrawable(model.getDrawable(activity))
    if (model.isSelected == false) {
      binding.image.makeGreyscale()
      binding.check.setImageResource(R.drawable.ic_unselected_blue)
    } else binding.check.setImageResource(R.drawable.ic_selected_blue)
  }


}
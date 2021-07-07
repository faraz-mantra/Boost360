package com.onboarding.nowfloats.holders.channel

import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.ItemChannelsConnectedBinding
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelsConnectViewHolder constructor(binding: ItemChannelsConnectedBinding) :
  AppBaseRecyclerViewHolder<ItemChannelsConnectedBinding>(binding) {

  private var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    model?.let { setViews(it) }
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.infoBtn -> listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.CHANNEL_CONNECT_INFO_CLICKED.ordinal
      )
    }
  }

  private fun setViews(model: ChannelModel) {
    setClickListeners(binding.infoBtn)
    binding.title.text = model.getName()
    binding.image.setImageDrawable(model.getDrawable(activity))
    binding.optInOut.gone()
    when {
      model.isWhatsAppChannel() -> {
        binding.optInOut.visible()
        binding.nameLink.text =
          model.channelActionData?.active_whatsapp_number?.takeIf { it.isNotEmpty() }?.let { it }
            ?: model.getName()
      }
      model.isGoogleBusinessChannel() -> {
        binding.nameLink.text =
          model.channelAccessToken?.userAccountName?.takeIf { it.isNotEmpty() }?.let { it }
            ?: model.getName()
//        binding.nameLink.text = model.channelAccessToken?.LocationName?.takeIf { it.isNotEmpty() }?.let { it } ?: model.getName()
      }
      model.isGoogleSearch() -> {
        binding.nameLink.text =
          model.websiteUrl?.takeIf { it.isNotEmpty() }?.let { it } ?: model.getName()
      }
      else -> binding.nameLink.text =
        model.channelAccessToken?.userAccountName?.takeIf { it.isNotEmpty() }?.let { it }
          ?: model.getName()
    }
  }
}
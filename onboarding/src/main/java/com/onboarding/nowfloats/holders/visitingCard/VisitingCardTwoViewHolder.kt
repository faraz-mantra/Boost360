package com.onboarding.nowfloats.holders.visitingCard

import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.databinding.ItemVisitingCardTwoBinding
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class VisitingCardTwoViewHolder(binding: ItemVisitingCardTwoBinding) : AppBaseRecyclerViewHolder<ItemVisitingCardTwoBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? DigitalCardData)?.cardData ?: return
    binding.businessName.text = data.businessName
    ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
    binding.number.text = data.number
    data.cardIcon?.let { binding.imgLogo.setImageResource(it) }
    if (data.businessLogo.isNullOrEmpty().not()) {
      binding.profileView.visible()
      activity?.glideLoad(binding.imgBusinessLogo, data.businessLogo!!)
    } else binding.profileView.gone()
    binding.email.text = fromHtml("<u>${data.email}</u>")
    binding.website.text = fromHtml("<u>${data.website}</u>")
  }
}
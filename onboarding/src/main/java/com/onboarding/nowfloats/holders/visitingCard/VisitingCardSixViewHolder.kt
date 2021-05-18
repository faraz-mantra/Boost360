package com.onboarding.nowfloats.holders.visitingCard

import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.databinding.ItemVisitingCardSixBinding
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class VisitingCardSixViewHolder(binding: ItemVisitingCardSixBinding) : AppBaseRecyclerViewHolder<ItemVisitingCardSixBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? DigitalCardData)?.cardData ?: return
    binding.businessName.text = data.businessName
    binding.number.text = data.number
    ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
    data.cardIcon?.let { binding.imgLogo.setImageResource(it)}
    if (data.businessLogo.isNullOrEmpty().not()) {
      binding.profileView.visible()
      binding.channels.gone()
      activity?.glideLoad(binding.imgBusinessLogo, data.businessLogo!!)
    } else {
      binding.profileView.gone()
      binding.channels.visible()
    }
    binding.email.text = fromHtml("<u>${data.email}</u>")
    binding.website.text = fromHtml("<u>${data.website}</u>")
  }
}
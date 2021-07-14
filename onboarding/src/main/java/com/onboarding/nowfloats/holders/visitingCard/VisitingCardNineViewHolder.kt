package com.onboarding.nowfloats.holders.visitingCard

import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.ItemVisitingCardNineBinding
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class VisitingCardNineViewHolder(binding: ItemVisitingCardNineBinding) :
  AppBaseRecyclerViewHolder<ItemVisitingCardNineBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? DigitalCardData)?.cardData ?: return
    binding.businessName.text = data.businessName
    binding.number.text = data.number
    data.cardIcon?.let { binding.imgLogo.setImageResource(it) }
    getColor(R.color.dusky_blue_1)?.let { binding.imgLogo.setTintColor(it) }
    if (data.businessLogo.isNullOrEmpty().not()) {
      binding.profileView.visible()
//      binding.channels.gone()
      activity?.glideLoad(binding.imgBusinessLogo, data.businessLogo!!)
    } else {
      binding.profileView.gone()
//      binding.channels.visible()
    }
    binding.email.text = fromHtml("<u>${data.email}</u>")
    binding.website.text = fromHtml("<u>${data.website}</u>")
  }
}
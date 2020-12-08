package com.onboarding.nowfloats.holders

import androidx.core.content.ContextCompat
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.databinding.ItemDigitalCardBinding
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class DigitalCardViewHolder(binding: ItemDigitalCardBinding) : AppBaseRecyclerViewHolder<ItemDigitalCardBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DigitalCardData ?: return
    binding.businessName.text = data.businessName
    binding.businessType.text = data.businessType
    binding.name.text = data.name
    binding.location.text = data.location
    binding.number.text = data.number
    activity!!.glideLoad(binding.imgBusinessLogo, data.businessLogo!!)
    binding.email.text = fromHtml("<u>${data.email}</u>")
    binding.website.text = fromHtml("<u>${data.website}</u>")
    binding.view1.backgroundTintList = ContextCompat.getColorStateList(activity!!, data.color1!!)
    binding.view2.backgroundTintList = ContextCompat.getColorStateList(activity!!, data.color2!!)
    binding.businessName.setTextColor(ContextCompat.getColor(activity!!, data.textColor1!!))
    binding.location.setTextColor(ContextCompat.getColor(activity!!, data.textColor2!!))
  }
}
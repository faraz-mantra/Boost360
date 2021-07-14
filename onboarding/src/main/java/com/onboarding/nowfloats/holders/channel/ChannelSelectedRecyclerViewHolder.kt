package com.onboarding.nowfloats.holders.channel

import com.onboarding.nowfloats.databinding.ItemSelectedChannelBinding
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem


class ChannelSelectedRecyclerViewHolder(binding: ItemSelectedChannelBinding) :
  AppBaseRecyclerViewHolder<ItemSelectedChannelBinding>(binding) {

  var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    setViews(model)
  }

  private fun setViews(model: ChannelModel?) {
    binding.image.setImageDrawable(model?.getDrawable(activity?.applicationContext))
    binding.image.makeGreyscale()
    val resources = getResources() ?: return
    val type = model?.getType() ?: return
//    var padding = resources.getDimension(R.dimen.size_8).toInt()
//    if (type == ChannelType.FB_SHOP || type == ChannelType.TWITTER_PROFILE) {
//      padding = 0
//    }
//    binding.image.setPadding(padding, padding, padding, padding)
  }
}
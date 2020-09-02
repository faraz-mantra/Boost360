package com.onboarding.nowfloats.holders.channel

import android.util.Log
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.ItemChannelBinding
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelRecyclerViewHolder(binding: ItemChannelBinding) : AppBaseRecyclerViewHolder<ItemChannelBinding>(binding) {

  var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    setViews(model)
  }

  private fun setViews(model: ChannelModel?) {
    val resources = getResources() ?: return
    val type = model?.getType() ?: return
    var padding = resources.getDimension(R.dimen.size_8).toInt()
//    if (type == ChannelType.FB_SHOP || type == ChannelType.TWITTER_PROFILE) {
//      padding = 0
//    }
//    binding.image.setPadding(padding, padding, padding, padding)
    binding.image.setImageDrawable(model.getDrawable(activity?.baseContext))
  }
}
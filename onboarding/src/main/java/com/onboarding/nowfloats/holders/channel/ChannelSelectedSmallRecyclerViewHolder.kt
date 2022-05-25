package com.onboarding.nowfloats.holders.channel

import android.util.Log
import com.onboarding.nowfloats.databinding.ItemSelectedChannelSmallBinding
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelSelectedSmallRecyclerViewHolder(binding: ItemSelectedChannelSmallBinding) :
  AppBaseRecyclerViewHolder<ItemSelectedChannelSmallBinding>(binding) {

  var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    setViews(model)
  }

  private fun setViews(model: ChannelModel?) {
    binding.image.setImageDrawable(model?.getDrawable(activity?.applicationContext))
//        if(model?.channelActionData == null && model?.isWhatsAppChannel()!!){
//            binding.image.makeGreyscale()
//        }else if(model.channelAccessToken == null && (model.isFacebookPage() || model.isFacebookShop() || model.isGoogleBusinessChannel() || model.isTwitterChannel())){
//            binding.image.makeGreyscale()
//        }
    if (model?.status != "SUCCESS" && model?.isWhatsAppChannel()!!) {
      binding.image.makeGreyscale()
    } else if (model.status != "SUCCESS" && (model.isFacebookPage() || model.isFacebookShop() ||
          model.isGoogleBusinessChannel() || model.isTwitterChannel()||model.isInstagram())
    ) {
      binding.image.makeGreyscale()
    }
    val resources = getResources() ?: return
    val type = model?.getType() ?: return
  }
}
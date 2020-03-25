package com.onboarding.nowfloats.holders.channel

import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getType
import com.onboarding.nowfloats.databinding.ItemSelectedChannelSmallBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelSelectedSmallRecyclerViewHolder(binding: ItemSelectedChannelSmallBinding) : AppBaseRecyclerViewHolder<ItemSelectedChannelSmallBinding>(binding) {

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
    }
}
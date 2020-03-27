package com.onboarding.nowfloats.holders.features

import SectionsFeature
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.ItemChannelFeatureBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelFeatureRecyclerViewHolder(binding: ItemChannelFeatureBinding) : AppBaseRecyclerViewHolder<ItemChannelFeatureBinding>(binding) {

    var model: SectionsFeature? = null

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        model = item as? SectionsFeature
        setViews(model)
    }

    private fun setViews(model: SectionsFeature?) {
        setClickListeners(binding.card)
        binding.title.text = model?.title
        takeIf { model?.desc.isNullOrEmpty() }?.let { binding.description.gone() } ?: binding.description.visible()
        binding.description.text = model?.desc
        val drawable = model?.getDrawable(activity) ?: return
        binding.image.setImageDrawable(drawable)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.card -> listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.FEATURE_ITEM_CLICKED.ordinal)
        }
    }
}
package com.onboarding.nowfloats.holders.features

import android.view.View
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.model.feature.FeatureModel
import com.onboarding.nowfloats.databinding.ItemChannelFeatureBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelFeatureRecyclerViewHolder(binding: ItemChannelFeatureBinding) : AppBaseRecyclerViewHolder<ItemChannelFeatureBinding>(binding) {

  var model: FeatureModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? FeatureModel
    setViews(model)
  }

  private fun setViews(model: FeatureModel?) {
    setClickListeners(binding.card)
    binding.title.text = model?.title
    binding.description.text = model?.description
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
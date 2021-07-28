package com.onboarding.nowfloats.holders.features

import DetailsFeature
import com.onboarding.nowfloats.databinding.ItemFeatureDetailsBottomSheetBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class FeatureDetailsRecyclerViewHolder(binding: ItemFeatureDetailsBottomSheetBinding) :
  AppBaseRecyclerViewHolder<ItemFeatureDetailsBottomSheetBinding>(binding) {

  var model: DetailsFeature? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? DetailsFeature
    setViews(model)
  }

  private fun setViews(model: DetailsFeature?) {
    binding.title.text = model?.title
    binding.desc.text = model?.desc
  }
}
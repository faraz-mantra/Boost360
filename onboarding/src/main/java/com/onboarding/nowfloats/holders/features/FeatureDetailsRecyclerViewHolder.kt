package com.onboarding.nowfloats.holders.features

import com.onboarding.nowfloats.model.feature.FeatureDetailsModel
import com.onboarding.nowfloats.databinding.ItemFeatureDetailsBottomSheetBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class FeatureDetailsRecyclerViewHolder(binding: ItemFeatureDetailsBottomSheetBinding) :
        AppBaseRecyclerViewHolder<ItemFeatureDetailsBottomSheetBinding>(binding) {

  var model: FeatureDetailsModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? FeatureDetailsModel
    setViews(model)
  }

  private fun setViews(model: FeatureDetailsModel?) {
    binding.title.text = model?.title
    binding.desc.text = model?.description
  }
}
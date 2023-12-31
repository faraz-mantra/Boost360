package com.onboarding.nowfloats.holders

import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.CityItemDataBinding
import com.onboarding.nowfloats.model.CityDataModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import java.util.*

class CityRecyclerViewHolder(binding: CityItemDataBinding) :
  AppBaseRecyclerViewHolder<CityItemDataBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model = item as? CityDataModel
    binding.city.text = model?.name!!.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    binding.state.text = model?.state
    binding.click.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.CITY_ITEM_CLICKED.ordinal
      )
    }
  }
}
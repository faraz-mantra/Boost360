package com.dashboard.holder

import android.view.View
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemGrowthStateBinding
import com.dashboard.model.GrowthStatsData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class GrowthStateViewHolder(binding: ItemGrowthStateBinding) : AppBaseRecyclerViewHolder<ItemGrowthStateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? GrowthStatsData ?: return
    getColor(if (position % 2 == 0) R.color.light_grey_4 else R.color.white)?.let { binding.mainContent.setBackgroundColor(it) }
    binding.txtTitle.text = data.title
    binding.txtAmount.text = data.value
    binding.txtAmountCountDay.text = data.dayAmountTitle
    data.icon1?.let { binding.imgIcon.setImageResource(it) }
    binding.txtSymbol.apply { visibility = if (data.isRupeeSymbols) View.VISIBLE else View.GONE }
    binding.txtAmountCountDay.apply { visibility = if (data.dayAmountTitle.isNullOrEmpty()) View.GONE else View.VISIBLE }
    binding.mainContent.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.GROWTH_STATS_CLICK.ordinal) }
  }

}

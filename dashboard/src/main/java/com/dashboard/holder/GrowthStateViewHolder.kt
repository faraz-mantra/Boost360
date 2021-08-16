package com.dashboard.holder

import android.view.View
import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemGrowthStateBinding
import com.dashboard.model.GrowthStatsData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class GrowthStateViewHolder(binding: ItemGrowthStateBinding) :
  AppBaseRecyclerViewHolder<ItemGrowthStateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? GrowthStatsData ?: return
    ContextCompat.getColorStateList(
      activity!!,
      if (position % 2 == 0) R.color.white_smoke_1 else R.color.white
    )?.let { binding.mainContent.backgroundTintList = it }
    binding.txtTitle.text = data.title
    binding.txtAmount.text = if (data.value.equals("0")) "_" else data.value
    binding.txtAmountCountDay.text = data.dayAmountTitle
    data.icon1?.let { binding.imgIcon.setImageResource(it) }
    binding.txtSymbol.apply { visibility = if (data.isRupeeSymbols) View.VISIBLE else View.GONE }
    binding.txtAmountCountDay.apply {
      visibility = if (data.dayAmountTitle.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.GROWTH_STATS_CLICK.ordinal
      )
    }
  }

}

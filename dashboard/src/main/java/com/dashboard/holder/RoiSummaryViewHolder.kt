package com.dashboard.holder

import android.view.View
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemRoiSummaryBinding
import com.dashboard.model.RoiSummaryData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class RoiSummaryViewHolder(binding: ItemRoiSummaryBinding) :
  AppBaseRecyclerViewHolder<ItemRoiSummaryBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? RoiSummaryData ?: return
    binding.txtSymbol.visibility = if (data.isRupeeSymbols) View.VISIBLE else View.GONE
    binding.txtTitle.text = data.title
    data.icon1?.let { binding.imgIcon.setImageResource(it) }
    binding.txtDayTitle.visibility =
      if (data.dayTitle.isNullOrEmpty().not()) View.VISIBLE else View.GONE
    binding.txtDayTitle.text = data.dayTitle
    binding.txtAmount.text = if (data.value.equals("0")) "_" else data.value
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.ROI_SUMMARY_CLICK.ordinal
      )
    }
  }

}

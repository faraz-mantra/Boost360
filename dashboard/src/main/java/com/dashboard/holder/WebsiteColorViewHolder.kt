package com.dashboard.holder

import android.graphics.Color
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.RecyclerItemColorsBinding
import com.dashboard.model.websitetheme.ColorsItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class WebsiteColorViewHolder(binding: RecyclerItemColorsBinding) : AppBaseRecyclerViewHolder<RecyclerItemColorsBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val colorsItem = item as? ColorsItem
    binding.ccvColor.setCardBackgroundColor(Color.parseColor(colorsItem?.primary))
    when (colorsItem?.isSelected) {
      true -> {
        binding.civCheck.visible()
      }
      else -> {
        binding.civCheck.gone()
      }
    }
    binding.root.setOnClickListener {
      colorsItem?.isSelected = true
      listener?.onItemClick(position,item,RecyclerViewActionType.ITEM_COLOR_CLICK.ordinal) }
  }
}
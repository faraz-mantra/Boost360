package com.dashboard.holder

import android.graphics.Color
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.RecyclerItemColorsBinding
import com.dashboard.model.websitetheme.ColorsItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class WebsiteColorViewHolder(binding: RecyclerItemColorsBinding) :
  AppBaseRecyclerViewHolder<RecyclerItemColorsBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val colorsItem = item as? ColorsItem
    binding.ccvColor.setCardBackgroundColor(Color.parseColor(colorsItem?.primary?.ifBlank { colorsItem.secondary }))
    when (colorsItem?.isSelected) {
      true -> {
        binding.civCheck.visible()
        binding.ccvColor.strokeWidth = 5
        binding.ccvColor.elevation = 2F
        binding.ccvColor.strokeColor =
          Color.parseColor(colorsItem.secondary?.ifBlank { colorsItem.primary })
      }
      else -> {
        binding.civCheck.gone()
        binding.ccvColor.strokeWidth = 0
        binding.ccvColor.elevation = 0F
        binding.ccvColor.strokeColor =
          Color.parseColor(colorsItem?.primary?.ifBlank { colorsItem.secondary })
      }
    }
    binding.root.setOnClickListener {
      colorsItem?.isSelected = true
      listener?.onItemClick(position, item, RecyclerViewActionType.ITEM_COLOR_CLICK.ordinal)
    }
  }
}
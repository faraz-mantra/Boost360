package com.dashboard.holder

import androidx.core.content.res.ResourcesCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.RecyclerItemSelectFontBinding
import com.dashboard.model.websitetheme.PrimaryItem
import com.dashboard.model.websitetheme.SecondaryItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class WebSiteFontViewHolder(binding: RecyclerItemSelectFontBinding) : AppBaseRecyclerViewHolder<RecyclerItemSelectFontBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    when (item) {
      is PrimaryItem -> {
        binding.ctvFontRadio.text = if (item.description.isNullOrEmpty().not()) item.description else "Empty Name $position"
        if (item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.semi_bold)
          binding.ctvFontRadio.typeface = font

        } else {
          binding.ctvFontCurrent.gone()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.regular)
          binding.ctvFontRadio.typeface = font

        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal)
        }
      }
      is SecondaryItem -> {
        binding.ctvFontRadio.text = if (item.description.isNullOrEmpty().not()) item.description else "Empty Name $position"
        if (item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.semi_bold)
          binding.ctvFontRadio.typeface = font

        } else {
          binding.ctvFontCurrent.gone()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.regular)
          binding.ctvFontRadio.typeface = font

        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal)
        }
      }
    }
  }
}
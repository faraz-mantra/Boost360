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
        binding.ctvFontTitle.text = if (item.description.isNullOrEmpty().not()) item.description else "Empty Name $position"
        if (item.isSelected == true) {
          binding.imgSelected.visible()
          binding.imgSelected.setTintColor(getColor(R.color.orange)!!)
          binding.ctvFontTitle.setTextColor(getColor(R.color.orange)!!)
          val font = ResourcesCompat.getFont(binding.root.context, R.font.semi_bold)
          binding.ctvFontTitle.typeface = font

        } else {
          binding.imgSelected.gone()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.regular)
          binding.ctvFontTitle.typeface = font
          binding.ctvFontTitle.setTextColor(getColor(R.color.black_2A2A2A)!!)

        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal)
        }
      }
      is SecondaryItem -> {
        binding.ctvFontTitle.text = if (item.description.isNullOrEmpty().not()) item.description else "Empty Name $position"
        if (item.isSelected == true) {
          binding.imgSelected.visible()
          binding.imgSelected.setTintColor(getColor(R.color.orange)!!)
          binding.ctvFontTitle.setTextColor(getColor(R.color.orange)!!)
          val font = ResourcesCompat.getFont(binding.root.context, R.font.semi_bold)
          binding.ctvFontTitle.typeface = font
        } else {
          binding.imgSelected.gone()
          val font = ResourcesCompat.getFont(binding.root.context, R.font.regular)
          binding.ctvFontTitle.typeface = font
          binding.ctvFontTitle.setTextColor(getColor(R.color.black_2A2A2A)!!)
        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal)
        }
      }
    }
  }
}
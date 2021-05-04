package com.dashboard.holder

import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
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
        binding.ctvFontTitle.text = item.description
        if (item.isSelected == true) {
          binding.imgSelected.visible()
          binding.imgSelected.setTintColor(getColor(R.color.orange)!!)

        } else {
          binding.imgSelected.gone()

        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal)
        }
      }
    }
    when (item) {
      is SecondaryItem -> {
        binding.ctvFontTitle.text = item.description
        if (item.isSelected == true) {
          binding.imgSelected.visible()
          binding.imgSelected.setTintColor(getColor(R.color.orange)!!)
        } else {
          binding.imgSelected.gone()

        }
        binding.root.setOnClickListener {
          listener?.onItemClick(position, item, RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal)
        }
      }

    }


  }
}
package com.dashboard.holder

import android.view.View
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.FilterDateModel
import com.dashboard.databinding.ItemFilterDateBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class DateFilterViewHolder(binding: ItemFilterDateBinding) : AppBaseRecyclerViewHolder<ItemFilterDateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? FilterDateModel ?: return
    getColor(if (data.isSelect) R.color.lightest_grey else R.color.white)?.let { binding.mainView.setBackgroundColor(it) }
    binding.title.text = data.title
    binding.imgSelected.visibility = if (data.isSelect) View.VISIBLE else View.INVISIBLE
    binding.mainView.setOnClickListener { listener?.onItemClick(position,item, RecyclerViewActionType.DATE_FILTER_CLICK.ordinal) }
    binding.invalidateAll()
  }
}

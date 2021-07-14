package com.dashboard.holder

import android.view.View
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemQuickActionBinding
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class QuickActionViewHolder(binding: ItemQuickActionBinding) :
  AppBaseRecyclerViewHolder<ItemQuickActionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? QuickActionItem ?: return
    binding.txtTitle.text = data.title
    binding.isNew.visibility = if (data.isNew) View.VISIBLE else View.INVISIBLE
    val type = QuickActionItem.QuickActionType.from(data.quickActionType)
    type?.icon?.let { binding.imgIcon.setImageResource(it) }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal
      )
    }
  }
}


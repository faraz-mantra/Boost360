package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemActionBinding
import com.dashboard.databinding.ItemQuickActionBinding
import com.dashboard.model.live.quickAction.QuickActionData
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener

class QuickActionViewHolder(binding: ItemQuickActionBinding) : AppBaseRecyclerViewHolder<ItemQuickActionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? QuickActionData ?: return
    data.items?.forEachIndexed { index, item ->
      when (index) {
        0 -> binding.includeOne.setData(index, item, listener)
        1 -> binding.includeTwo.setData(index, item, listener)
        2 -> binding.includeThree.setData(index, item, listener)
        3 -> binding.includeFour.setData(index, item, listener)
      }
    }
  }
}

private fun ItemActionBinding.setData(index: Int, item: QuickActionItem, listener: RecyclerItemClickListener?) {
  this.txtTitle.text = item.title
  val type = QuickActionData.QuickActionType.from(item.quickActionType)
  type?.icon?.let { this.imgIcon.setImageResource(it) }
  this.mainContent.setOnClickListener { listener?.onItemClick(index, item, RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal) }
}

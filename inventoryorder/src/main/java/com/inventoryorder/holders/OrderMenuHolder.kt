package com.inventoryorder.holders

import android.view.View
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderMenuBinding
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderMenuHolder(binding: ItemOrderMenuBinding) : AppBaseRecyclerViewHolder<ItemOrderMenuBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as OrderMenuModel
    val menuType = OrderMenuModel.MenuStatus.from(data.type) ?: return
    binding.title.text = menuType.title
    getColor(menuType.color)?.let { binding.title.setTextColor(it) }
    binding.line1.visibility = if (data.endLine) View.VISIBLE else View.GONE
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.ORDER_DROPDOWN_CLICKED.ordinal
      )
    }
  }
}

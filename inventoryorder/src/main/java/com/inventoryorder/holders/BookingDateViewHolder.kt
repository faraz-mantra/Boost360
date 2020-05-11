package com.inventoryorder.holders

import android.view.View
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.getAmountDate
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.databinding.ItemBookingsDateBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class BookingDateViewHolder(binding: ItemBookingsDateBinding) : AppBaseRecyclerViewHolder<ItemBookingsDateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    binding.view.visibility = if (adapterPosition != 0) View.VISIBLE else View.GONE
    val data = item as? OrderItem
    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val yesterdayDate = getAmountDate(-1).parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val itemDate = data?.dateKey?.parseDate(FORMAT_DD_MM_YYYY) ?: ""
    binding.date.text = when {
      (todayDate == itemDate) -> "TODAY"
      (yesterdayDate == itemDate) -> "YESTERDAY"
      else -> itemDate
    }
  }
}

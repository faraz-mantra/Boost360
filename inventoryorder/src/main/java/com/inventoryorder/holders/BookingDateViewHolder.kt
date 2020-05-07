package com.inventoryorder.holders

import android.view.View
import com.inventoryorder.databinding.ItemBookingsDateBinding
import com.inventoryorder.model.bookingdetails.BookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import java.util.*

class BookingDateViewHolder(binding: ItemBookingsDateBinding) : AppBaseRecyclerViewHolder<ItemBookingsDateBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    binding.view.visibility = if (adapterPosition != 0) View.VISIBLE else View.GONE
    val data = item as? BookingsModel
    binding.date.text = data?.date?.toUpperCase(Locale.ROOT)
  }
}

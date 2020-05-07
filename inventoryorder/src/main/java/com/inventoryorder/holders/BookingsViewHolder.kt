package com.inventoryorder.holders

import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBookingsAllOrderBinding
import com.inventoryorder.model.bookingdetails.BookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class BookingsViewHolder(binding: ItemBookingsAllOrderBinding) : AppBaseRecyclerViewHolder<ItemBookingsAllOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as BookingsModel
    if (adapterPosition == 4) {
      binding.orderType.text = "New Booking"
      activity?.let {
        binding.orderType.background = ContextCompat.getDrawable(it, R.drawable.cancel_order_bg)
        val colorPrimary = ContextCompat.getColor(it, R.color.primary_grey)
        binding.orderId.setTextColor(colorPrimary)
        binding.tvOrderAmount.setTextColor(colorPrimary)
        binding.tvOrderDate.setTextColor(colorPrimary)
        binding.tvPaymentMode.setTextColor(colorPrimary)
        binding.tvCustomersLocation.setTextColor(colorPrimary)
        binding.itemDesc.setTextColor(colorPrimary)
        binding.itemMore.setTextColor(colorPrimary)
        binding.buttonConfirm.setTextColor(colorPrimary)
      }
      binding.buttonConfirm.paintFlags = binding.buttonConfirm.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
      activity?.let { binding.buttonConfirm.background = ContextCompat.getDrawable(it, R.drawable.btn_rounded_grey_border) }
      binding.viewTime.gone()
    } else if (adapterPosition == 5 || adapterPosition == 6) {
      activity?.let {
        val colorPrimary = ContextCompat.getColor(it, R.color.primary_grey)
        binding.tvOrderAmount.setTextColor(colorPrimary)
        binding.orderId.setTextColor(colorPrimary)
        binding.orderType.background = ContextCompat.getDrawable(it, R.drawable.cancel_order_bg)
      }
      binding.orderType.text = "Cancelled"
      binding.detailsOrder.gone()
      binding.buttonConfirm.gone()
      binding.next2.visible()
    }
    binding.itemMore.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG).let { binding.itemMore.paintFlags = it }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
    }
  }


}
package com.inventoryorder.holders

import android.graphics.Paint
import com.framework.glide.util.glideLoad
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemBookingDetailsBinding
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AppointmentDetailsViewHolder(binding: ItemBookingDetailsBinding) : AppBaseRecyclerViewHolder<ItemBookingDetailsBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ItemN
    data?.let { setDataResponseForOrderDetails(it) }
  }

  private fun setDataResponseForOrderDetails(item: ItemN) {
    binding.tvDishName.text = item.Product?.Name?.trim()
    binding.tvDishQuantity.text = "Qty: ${item.Quantity}"
    val currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
    binding.tvDishAmount.text = "$currency ${item.SalePrice ?: 0}"
    binding.tvActualPrice.paintFlags = binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    binding.tvActualPrice.text = "$currency ${item.ActualPrice ?: 0}"
    val url: String? = item.Product?.ImageUri?.trim()
    url?.let { activity?.glideLoad(binding.ivDishItem, it, R.drawable.placeholder_image) }
        ?: (binding.ivDishItem.setImageResource(R.drawable.placeholder_image))

  }
}
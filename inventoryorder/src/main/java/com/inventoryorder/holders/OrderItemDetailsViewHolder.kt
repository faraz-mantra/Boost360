package com.inventoryorder.holders

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import com.framework.glide.util.glideLoad
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemOrderDetailsBinding
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemDetailsViewHolder(binding: ItemOrderDetailsBinding) : AppBaseRecyclerViewHolder<ItemOrderDetailsBinding>(binding) {


  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as ItemN
    setDataResponseForOrderDetails(data)
  }

  @SuppressLint("SetTextI18n")
  private fun setDataResponseForOrderDetails(item: ItemN) {
    binding.tvDishName.text = item.Product?.Name?.trim()
    binding.tvDishQuantity.text = "Qty: ${item.Quantity}"
    val currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
    val actualPrice = item.product().price()
    val salePrice = actualPrice - item.product().discountAmount()
    binding.tvDishAmount.text = "$currency $salePrice"
    if (actualPrice > 0.0 && actualPrice > salePrice) {
      binding.tvActualPrice.paintFlags = binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
      binding.tvActualPrice.text = "$currency $actualPrice"
      binding.tvActualPrice.visibility = View.VISIBLE
    } else binding.tvActualPrice.visibility = View.INVISIBLE
    var url: String? = item.product_detail?.ImageUri?.trim()
    if (url.isNullOrEmpty()) url = item.Product?.ImageUri?.trim()
    url?.let { activity?.glideLoad(binding.ivDishItem, it, R.drawable.placeholder_image) }
        ?: (binding.ivDishItem.setImageResource(R.drawable.placeholder_image))
  }

}

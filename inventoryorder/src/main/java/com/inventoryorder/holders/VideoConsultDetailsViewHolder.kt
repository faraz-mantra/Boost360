package com.inventoryorder.holders

import android.graphics.Paint
import com.inventoryorder.databinding.ItemVideoConsultDetailsBinding
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class VideoConsultDetailsViewHolder(binding: ItemVideoConsultDetailsBinding) : AppBaseRecyclerViewHolder<ItemVideoConsultDetailsBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ItemN
    data?.let { setDataResponseForOrderDetails(it) }
  }

  private fun setDataResponseForOrderDetails(item: ItemN) {
    binding.tvDishName.text = item.Product?.extraItemProductConsultation()?.detailsConsultation()
    binding.tvDishQuantity.text = item.Product?.extraItemProductConsultation()?.durationTxt() ?: "0 Minute"
    val currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
    binding.tvDishAmount.text = "$currency ${item.SalePrice ?: 0}"
    binding.tvActualPrice.paintFlags = binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    binding.tvActualPrice.text = "$currency ${item.ActualPrice ?: 0}"
//    val url: String? = item.Product?.ImageUri?.trim()
//    url?.let { activity?.glideLoad(binding.ivDishItem, it, R.drawable.placeholder_image) }
//        ?: (binding.ivDishItem.setImageResource(R.drawable.placeholder_image))

  }
}
package com.inventoryorder.holders

import android.os.Build
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemOrderTypeBinding
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderSummaryViewHolder(binding: ItemOrderTypeBinding) : AppBaseRecyclerViewHolder<ItemOrderTypeBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderSummaryModel
    binding.title.text = data?.type
    binding.count.text = data?.count?.toString()
    if (adapterPosition == 1) {
      activity?.let {
        binding.title.setTextColor(ContextCompat.getColor(it, R.color.white))
        binding.count.setTextColor(ContextCompat.getColor(it, R.color.black))
        binding.mainView.background = ContextCompat.getDrawable(it, R.drawable.bg_rounded_solid_30)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          binding.count.backgroundTintList = ContextCompat.getColorStateList(it, R.color.white)
        }
      }
    }
  }
}
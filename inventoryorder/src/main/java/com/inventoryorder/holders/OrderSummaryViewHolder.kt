package com.inventoryorder.holders

import android.os.Build
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderTypeBinding
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderSummaryViewHolder(binding: ItemOrderTypeBinding) :
  AppBaseRecyclerViewHolder<ItemOrderTypeBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? OrderSummaryModel) ?: return
    binding.title.text = data.type
    val count = data.count?.toString()?.trim()
    binding.count.text = count?.takeIf { it.length < 2 }?.let { " $count " } ?: count
    activity?.let {
      binding.title.setTextColor(ContextCompat.getColor(it,
        takeIf { data.isSelected.not() }?.let { R.color.black } ?: (R.color.white))
      )
      binding.count.setTextColor(ContextCompat.getColor(it,
        takeIf { data.isSelected.not() }?.let { R.color.white } ?: (R.color.black))
      )
      binding.mainView.background = ContextCompat.getDrawable(it,
        takeIf { data.isSelected.not() }?.let { R.drawable.bg_rounded_30 }
          ?: (R.drawable.bg_rounded_solid_30)
      )

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        binding.count.backgroundTintList = ContextCompat.getColorStateList(it,
          takeIf { data.isSelected.not() }?.let { data.color ?: R.color.colorAccent }
            ?: (R.color.white)
        )
      }


//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        binding.count.backgroundTintList = ContextCompat.getColorStateList(it,
//            takeIf { data.isSelected.not() }?.let { R.color.colorAccent } ?: (R.color.white)
//        )
//      }
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.ORDER_SUMMARY_CLICKED.ordinal
      )
    }
  }
}
package com.inventoryorder.holders

import android.os.Build
import androidx.core.content.ContextCompat
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.model.InventoryOrderModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemViewHolder(binding: ItemOrderBinding) : AppBaseRecyclerViewHolder<ItemOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? InventoryOrderModel
    binding.order.title.text = "Order ID: "
    binding.payment.title.text = "Payment:"
    binding.delivery.title.text = "Delivery:  "
    binding.order.value.text = "GK7C4FM"
    binding.payment.value.text = "COD, Pending"

    when (data?.orderType) {
      InventoryOrderModel.TYPE.NEW_ORDER.name -> {
        binding.delivery.value.text = "Pickup"
        binding.orderType.text = "New Order"
      }
      InventoryOrderModel.TYPE.COMPLETED.name -> {
        binding.delivery.value.text = "Home Delivery"
        binding.orderType.text = "Completed"
      }
      InventoryOrderModel.TYPE.CANCELLED.name -> {
        binding.delivery.value.text = "Pickup"
        binding.orderType.text = "Cancelled"
        binding.itemMore.visible()
        binding.itemCount.text = "8 Items:"
        binding.itemMore.text = "+5 more"
        binding.itemCount.text = "8 Items:"
        binding.timeElapsed.text = "12-Mar '20 2:30 PM"
        binding.statusTime.text = "Updated at"
        binding.btnClick.text = "READY"
        activity?.let {
          binding.itemDesc.setTextColor(ContextCompat.getColor(it, R.color.warm_grey_10))
          binding.itemMore.setTextColor(ContextCompat.getColor(it, R.color.warm_grey_10))
          binding.itemMore.background = ContextCompat.getDrawable(it, R.drawable.line_bac)
          binding.btnClick.background = ContextCompat.getDrawable(it, R.drawable.btn_rounded_blue_6)
          binding.btnClick.setTextColor(ContextCompat.getColor(it, R.color.light_blue))

          binding.timeElapsed.setTextColor(ContextCompat.getColor(it, R.color.black))
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timeElapsed.compoundDrawableTintList = ContextCompat.getColorStateList(it, R.color.warm_grey_10)
          }
        }
      }
      InventoryOrderModel.TYPE.DELAYED.name -> {
        binding.delivery.value.text = "Home Delivery"
        binding.orderType.text = "Delayed"
      }
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal)
    }
  }
}
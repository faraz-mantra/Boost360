package com.inventoryorder.holders

import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemViewHolder(binding: ItemOrderBinding) : AppBaseRecyclerViewHolder<ItemOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let {
      binding.orderDate.title.text = "Date:       "
      binding.payment.title.text = "Payment:"
      binding.delivery.title.text = "Delivery:  "
//      setDataResponse(it)
    }
//    when (data?.orderType) {
//      InventoryOrderModel.TYPE.NEW_ORDER.name -> {
//        binding.delivery.value.text = "Pickup"
//        binding.orderType.text = "New Order"
//      }
//      InventoryOrderModel.TYPE.COMPLETED.name -> {
//        binding.delivery.value.text = "Home Delivery"
//        binding.orderType.text = "Completed"
//      }
//      InventoryOrderModel.TYPE.CANCELLED.name -> {
//        binding.delivery.value.text = "Pickup"
//        binding.orderType.text = "Cancelled"
//        binding.itemMore.visible()
//        binding.itemCount.text = "8 Items:"
//        binding.itemMore.text = "+5 more"
//        binding.itemCount.text = "8 Items:"
//        activity?.let {
//          binding.itemDesc.setTextColor(ContextCompat.getColor(it, R.color.warm_grey_10))
//          binding.itemMore.setTextColor(ContextCompat.getColor(it, R.color.warm_grey_10))
//          binding.itemMore.background = ContextCompat.getDrawable(it, R.drawable.line_bac)
////          binding.btnClick.background = ContextCompat.getDrawable(it, R.drawable.btn_rounded_white_4)
////          binding.btnClick.setTextColor(ContextCompat.getColor(it, R.color.light_blue))
////
////          binding.timeElapsed.setTextColor(ContextCompat.getColor(it, R.color.black))
////          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            binding.timeElapsed.compoundDrawableTintList = ContextCompat.getColorStateList(it, R.color.warm_grey_10)
////          }
//        }
//      }
//      InventoryOrderModel.TYPE.DELAYED.name -> {
//        binding.delivery.value.text = "Home Delivery"
//        binding.orderType.text = "Delayed"
//      }
//    }
//    binding.mainView.setOnClickListener {
//      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal)
//    }
  }

  private fun setDataResponse(order: OrderItem) {
    binding.orderType.text = OrderSummaryModel.OrderType.fromValue(order.status()).type
    binding.orderType.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding.txtRupees.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding.orderDate.value.text = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL)
    binding.payment.value.text = order.PaymentDetails?.Method
    binding.delivery.value.text = order.LogisticsDetails?.DeliveryMode
    val sizeItem = order.Items?.size ?: 0
    binding.itemCount.text = "$sizeItem ${takeIf { sizeItem > 1 }?.let { "Items" } ?: "Item"}"
//    binding.itemDesc.text = order.getTitles()

    when (OrderSummaryModel.OrderType.fromValue(order.status())) {
      OrderSummaryModel.OrderType.RECEIVED -> {
      }
      OrderSummaryModel.OrderType.SUCCESSFUL -> {
      }
      OrderSummaryModel.OrderType.CANCELLED -> {
      }
      OrderSummaryModel.OrderType.RETURNED -> {
      }
      OrderSummaryModel.OrderType.ABANDONED -> {
      }
      OrderSummaryModel.OrderType.ESCALATED -> {
      }
    }
  }
}
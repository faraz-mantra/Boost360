package com.inventoryorder.ui.order

import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding
import com.inventoryorder.model.ordersdetails.ItemX
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter


class InventoryOrderDetailFragment : BaseOrderFragment<FragmentInventoryOrderDetailBinding>() {

  private var orderItem: OrderItem? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InventoryOrderDetailFragment {
      val fragment = InventoryOrderDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    orderItem = arguments?.getSerializable(IntentConstant.ORDER_ITEM.name) as? OrderItem
    orderItem?.let { setDetails(it) }

  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("# ${order.ReferenceNumber}")
    setOrderDetails(order)
    order.Items?.let { setAdapter(it) }
  }

  private fun setAdapter(orderItems: ArrayList<ItemX>) {
    binding?.recyclerViewOrderDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewOrderDetails?.adapter = adapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_share)
    item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
      showLongToast("Share")
    }
  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = OrderSummaryModel.OrderType.fromValue(order.status()).type
    binding?.tvOrderStatus?.text = order.PaymentDetails?.Status?.trim()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.Method?.trim()
    binding?.tvDeliveryPaymentStatus?.text = "Status: ${order.PaymentDetails?.Status?.trim()}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding?.tvOrderPlacedDate?.text = DateUtils.parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL_2)

    // customer details
    binding?.tvCustomerName?.text = order.BuyerDetails?.ContactDetails?.FullName?.trim()
    binding?.tvCustomerAddress?.text = order.BuyerDetails?.getAddressFull()

    binding?.tvCustomerContactNumber?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerContactNumber?.setPaintFlags(it) }
    binding?.tvCustomerEmail?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerEmail?.setPaintFlags(it) }
    binding?.tvCustomerContactNumber?.text = order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()
    binding?.tvCustomerEmail?.text = order.BuyerDetails?.ContactDetails?.EmailId?.trim()

    // shipping details
    var shippingCost = 0.0
    var salePrice = 0.0
    var currency = "INR"
    order.Items?.forEachIndexed { index, item ->
      shippingCost += item.ShippingCost ?: 0.0
      salePrice += item.SalePrice ?: 0.0
      if (index == 0) currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }
          ?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
    }
    binding?.tvShippingCost?.text = "Shipping Cost: $currency $shippingCost"
    binding?.tvTotalOrderAmount?.text = "Total Amount: $currency $salePrice"


  }
}
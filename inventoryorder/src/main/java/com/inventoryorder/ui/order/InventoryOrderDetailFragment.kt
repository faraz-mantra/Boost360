package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding
import com.inventoryorder.model.ordersdetails.ItemX
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetails
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

    setOrderDetails(orderItem)
  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("# ${order.ReferenceNumber}")
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

  private fun setOrderDetails(order: OrderItem?){

    binding?.tvOrderStatus?.text = order?.PaymentDetails?.Status
    binding?.tvPaymentMode?.text = order?.PaymentDetails?.Method
    binding?.tvOrderAmount?.text = "₹"+order?.BillingDetails?.AmountPayableByBuyer.toString()
    binding?.tvOrderPlacedDate?.text = order?.CreatedOn

// customer details
    binding?.tvCustomerName?.text = order?.BuyerDetails?.ContactDetails?.FullName
    binding?.tvCustomerAddress?.text = order?.BuyerDetails?.ContactDetails?.PrimaryContactNumber+", "+order?.BuyerDetails?.ContactDetails?.EmailId+"\n"+
            order?.BuyerDetails?.Address?.AddressLine1+"- "+ order?.BuyerDetails?.Address?.AddressLine2+",\n" +order?.BuyerDetails?.Address?.Region+" "+
            order?.BuyerDetails?.Address?.Zipcode

    binding?.tvCustomerContactNumber?.text = order?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
    binding?.tvCustomerEmail?.text = order?.BuyerDetails?.ContactDetails?.EmailId

//shipping details
    binding?.tvShippingCost?.text = "Shipping Cost:"+order?.Items?.get(0)?.ShippingCost
    binding?.tvTotalOrderAmount?.text = "Total Amount:"+order?.Items?.get(0)?.SalePrice


  }
}
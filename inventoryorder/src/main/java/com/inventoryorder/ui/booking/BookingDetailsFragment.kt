package com.inventoryorder.ui.booking

import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.framework.utils.DateUtils
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentInventoryBookingDetailsBinding
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.model.ordersdetails.ItemX
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.DeliveryBottomSheetDialog

class BookingDetailsFragment : BaseInventoryFragment<FragmentInventoryBookingDetailsBinding>() {

  private var locationsBottomSheetDialog: LocationBottomSheetDialog? = null
  private var orderItem: OrderItem? = null
  private var serviceLocationsList = LocationsModel().getData()

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BookingDetailsFragment {
      val fragment = BookingDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    orderItem = arguments?.getSerializable(IntentConstant.BOOKING_ITEM.name) as? OrderItem
    orderItem?.let { setDetails(it) }
    setOnClickListener(binding?.btnBusiness, binding?.buttonConfirmOrder, binding?.tvCancelOrder)
  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("# ${order.ReferenceNumber}")
    setOrderDetails(order)
    (order.Items?.map {
      it.recyclerViewType = RecyclerViewItemType.BOOKING_DETAILS.getLayout();it
    } as? ArrayList<ItemX>)?.let { setAdapter(it) }
  }

  private fun setAdapter(orderItems: ArrayList<ItemX>) {
    binding?.recyclerViewBookingDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewBookingDetails?.adapter = adapter
    }
  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = OrderSummaryModel.OrderType.fromValue(order.status())?.type
    binding?.tvOrderStatus?.text = order.PaymentDetails?.Status?.trim()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.Method?.trim()
    binding?.tvDeliveryPaymentStatus?.text = "Status: ${order.PaymentDetails?.Status?.trim()}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() }
          ?: "INR"
      binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding?.bookingDate?.text = DateUtils.parseDate(order.CreatedOn, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2)

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

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnBusiness -> showBottomSheetDialog()
      binding?.buttonConfirmOrder -> showShortToast("Coming Soon...")
      binding?.tvCancelOrder -> showShortToast("Coming Soon...")

    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_share)
    item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
      showLongToast("Coming soon..")
    }
  }


  private fun showBottomSheetDialog() {
    locationsBottomSheetDialog = LocationBottomSheetDialog()
    locationsBottomSheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
    locationsBottomSheetDialog?.setList(serviceLocationsList)
    locationsBottomSheetDialog?.show(this.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
  }

  private fun clickDeliveryItem(list: LocationsModel?) {
    serviceLocationsList.forEach { it.isSelected = (it.serviceOptionSelectedName == list?.serviceOptionSelectedName) }
  }
}
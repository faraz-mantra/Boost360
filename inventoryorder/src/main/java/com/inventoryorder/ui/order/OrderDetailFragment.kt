package com.inventoryorder.ui.order

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.bottomsheet.DeliveryModel
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import java.util.*

class OrderDetailFragment : BaseInventoryFragment<FragmentInventoryOrderDetailBinding>() {

  private var deliverySheetDialog: DeliveryBottomSheetDialog? = null
  private var orderItem: OrderItem? = null
  private var deliveryList = DeliveryModel().getData()
  private var isRefresh: Boolean? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): OrderDetailFragment {
      val fragment = OrderDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let { apiGetOrderDetails(it) }
    setOnClickListener(binding?.btnPickUp)
  }

  private fun apiGetOrderDetails(orderId: String) {
    showProgress()
    viewModel?.getOrderDetails(clientId, orderId)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        binding?.mainView?.visible()
        binding?.error?.gone()
        orderItem = (it as? OrderDetailResponse)?.Data
        if (orderItem != null) {
          setDetails(orderItem!!)
        } else errorUi("Order item null.")
      } else errorUi(it.message())
    })
  }

  private fun errorUi(message: String) {
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("# ${order.ReferenceNumber}")
    checkPaymentConfirm(order)
    checkCancelConfirm(order)
    setOrderDetails(order)
    order.Items?.let { setAdapter(it) }
  }


  private fun setAdapter(orderItems: ArrayList<ItemN>) {
    binding?.recyclerViewOrderDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewOrderDetails?.adapter = adapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_share)
    item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
      showLongToast("Coming soon..")
    }
  }

  private fun checkPaymentConfirm(order: OrderItem) {
    if (order.isConfirmBooking()) {
      buttonDisable(R.color.colorAccent)
      binding?.buttonConfirmOrder?.setOnClickListener(this)
    } else {
      buttonDisable(R.color.primary_grey)
      binding?.let { it.buttonConfirmOrder.paintFlags = it.buttonConfirmOrder.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG }
      binding?.buttonConfirmOrder?.setOnClickListener(null)
    }
  }

  fun getBundleData(): Bundle? {
    isRefresh?.let {
      val bundle = Bundle()
      bundle.putBoolean(IntentConstant.IS_REFRESH.name, it)
      return bundle
    }
    return null
  }

  private fun buttonDisable(color: Int) {
    activity?.let {
      val newDrawable: Drawable? = binding?.buttonConfirmOrder?.background
      newDrawable?.let { it1 -> DrawableCompat.setTint(it1, ContextCompat.getColor(it, color)) }
      binding?.buttonConfirmOrder?.background = newDrawable
    }
  }

  private fun checkCancelConfirm(order: OrderItem) {
    if (order.isCancelBooking()) {
      binding?.tvCancelOrder?.visible()
      binding?.tvCancelOrder?.setOnClickListener(this)
    } else binding?.tvCancelOrder?.gone()

  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = getStatusText(OrderSummaryModel.OrderType.fromValue(order.status()), order.PaymentDetails)
    binding?.tvOrderStatus?.text = order.PaymentDetails?.Status?.trim()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.Method?.trim()
    binding?.tvDeliveryPaymentStatus?.text = "Status: ${order.PaymentDetails?.Status?.trim()}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() }
          ?: "INR"
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

  private fun getStatusText(orderType: OrderSummaryModel.OrderType?, paymentDetails: PaymentDetailsN?): String? {
    return if (orderType == OrderSummaryModel.OrderType.CANCELLED
        && paymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
      OrderSummaryModel.OrderType.ABANDONED.type
    } else orderType?.type
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPickUp -> showBottomSheetDialog()
      binding?.buttonConfirmOrder -> apiConfirmOrder()
      binding?.tvCancelOrder -> apiCancelOrder()
    }
  }

  private fun apiCancelOrder() {
    showProgress()
    viewModel?.cancelOrder(clientId, orderItem?._id, OrderItem.CancellingEntity.SELLER.name)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }

      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val data = it as? OrderConfirmStatus
        isRefresh = true
        data?.let { d -> showLongToast(d.Message as String?) }
        orderItem?.Status = OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name
        orderItem?.let { it1 -> checkCancelConfirm(it1) }
      } else showLongToast(it.message())
    })
  }

  private fun apiConfirmOrder() {
    showProgress()
    viewModel?.confirmOrder(clientId, orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val data = it as? OrderConfirmStatus
        isRefresh = true
        data?.let { d -> showLongToast(d.Message as String?) }
        orderItem?.Status = OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name
        orderItem?.let { it1 -> checkPaymentConfirm(it1) }
      } else showLongToast(it.message())
    })
  }

  private fun showBottomSheetDialog() {
    deliverySheetDialog = DeliveryBottomSheetDialog()
    deliverySheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
    deliverySheetDialog?.setList(deliveryList)
    deliverySheetDialog?.show(this@OrderDetailFragment.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
  }

  private fun clickDeliveryItem(deliveryItem: DeliveryModel?) {
    deliveryList.forEach { it.isSelected = (it.deliveryOptionSelectedName == deliveryItem?.deliveryOptionSelectedName) }
  }
}
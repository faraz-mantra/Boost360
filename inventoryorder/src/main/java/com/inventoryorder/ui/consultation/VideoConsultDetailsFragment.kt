package com.inventoryorder.ui.consultation

import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentVideoConsultDetailsBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import java.util.*

class VideoConsultDetailsFragment : BaseInventoryFragment<FragmentVideoConsultDetailsBinding>() {

  private var orderItem: OrderItem? = null
  private var serviceLocationsList = LocationsModel().getData()
  private var isRefresh: Boolean? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): VideoConsultDetailsFragment {
      val fragment = VideoConsultDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let { apiGetOrderDetails(it) }
    setOnClickListener(binding?.btnPaymentReminder, binding?.btnCopyLink, binding?.btnOpenConsult)
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
    checkStatusConsultation(order)
    setOrderDetails(order)
    (order.Items?.map {
      it.recyclerViewType = RecyclerViewItemType.VIDEO_CONSULT_DETAILS.getLayout();it
    } as? ArrayList<ItemN>)?.let { setAdapter(it) }
  }

  private fun isOpenForConsultation(order: OrderItem) {
    val isOpen = order.isConfirmConsulting()
    binding?.bookingDate?.setTextColor(takeIf { isOpen }?.let { getColor(R.color.light_green) } ?: getColor(R.color.primary_grey))
    if (isOpen) isVisible(binding?.btnPaymentReminder, binding?.btnCopyLink, binding?.bottomView)
    else isGone(binding?.btnPaymentReminder, binding?.btnCopyLink, binding?.bottomView)
  }

  private fun setAdapter(orderItems: ArrayList<ItemN>) {
    binding?.recyclerViewBookingDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewBookingDetails?.adapter = adapter
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

  private fun checkStatusConsultation(order: OrderItem) {
    isOpenForConsultation(order)
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

  private fun getStatusText(orderType: OrderSummaryModel.OrderType?, paymentDetails: PaymentDetailsN?): String? {
    return if (orderType == OrderSummaryModel.OrderType.CANCELLED
        && paymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
      OrderSummaryModel.OrderType.ABANDONED.type
    } else orderType?.type
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPaymentReminder -> paymentReminder()
      binding?.btnOpenConsult -> apiOpenConsultationWindow()
      binding?.tvCancelOrder -> apiCancelOrder()
      binding?.btnCopyLink -> showLongToast("Coming soon..")
    }
  }

  private fun apiOpenConsultationWindow() {
    showLongToast("Coming soon..")
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
        orderItem?.let { it1 -> checkStatusConsultation(it1) }
      } else showLongToast(it.message())
    })
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_share)
    item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
      showLongToast("Coming soon..")
    }
  }

  private fun paymentReminder() {
    showLongToast("Coming soon..")
  }

  private fun clickDeliveryItem(list: LocationsModel?) {
    serviceLocationsList.forEach { it.isSelected = (it.serviceOptionSelectedName == list?.serviceOptionSelectedName) }
  }
}
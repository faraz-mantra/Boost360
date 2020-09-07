package com.inventoryorder.ui.consultation

import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.ValidationUtils
import com.framework.utils.ValidationUtils.isEmailValid
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentVideoConsultDetailsBinding
import com.inventoryorder.model.CLIENT_ID_3
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.SendMailRequest
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.utils.copyClipBoard
import com.inventoryorder.utils.openWebPage
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
    val isOpen = order.isConfirmConsultBtn()
//    val isOpen = true
    binding?.bookingDate?.setTextColor(takeIf { isOpen }?.let { getColor(R.color.light_green) } ?: getColor(R.color.primary_grey))
    if (isOpen) isVisible(binding?.btnPaymentReminder, binding?.btnCopyLink, binding?.bottomView)
    else isGone(binding?.btnPaymentReminder, binding?.btnCopyLink, binding?.bottomView)

    binding?.btnCopyLink?.visibility = View.GONE
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
    if (order.isCancelActionBtn()) {
      binding?.tvCancelOrder?.visible()
      binding?.tvCancelOrder?.setOnClickListener(this)
    } else binding?.tvCancelOrder?.gone()
  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = getStatusText(order)
    binding?.tvStatus?.text = order.PaymentDetails?.status()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.methodValue()
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "₹"
      binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    val scheduleDate = order.firstItemForConsultation()?.scheduledStartDate()
    val dateApt = DateUtils.parseDate(scheduleDate, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2)
    binding?.bookingDate?.text = if (dateApt.isNullOrEmpty().not()) {
      dateApt
    } else {
      DateUtils.parseDate(order.CreatedOn, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2, timeZone = TimeZone.getTimeZone("IST"))
    }

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
      shippingCost += item.Product?.ShippingCost ?: 0.0
      salePrice += item.product().price() - item.product().discountAmount()
      if (index == 0) currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }
          ?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
    }
    binding?.tvTotalOrderAmount?.text = "Total Amount: $currency $salePrice"

  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPaymentReminder -> paymentReminder()
      binding?.btnOpenConsult -> apiOpenConsultationWindow()
      binding?.tvCancelOrder -> apiCancelOrder()
      binding?.btnCopyLink -> videoConsultCopy()
    }
  }

  private fun videoConsultCopy() {
    orderItem?.consultationJoiningUrl(preferenceData?.webSiteUrl)?.let {
      if (baseActivity.copyClipBoard(it)) showLongToast(resources.getString(R.string.copied_patient_url))
      else showLongToast(resources.getString(R.string.error_copied_patient_url))
    }
  }

  private fun apiOpenConsultationWindow() {
    orderItem?.consultationWindowUrlForDoctor()?.let {
      if (baseActivity.openWebPage(it).not()) showLongToast(resources.getString(R.string.error_opening_consultation_window))
    }
  }

  private fun apiCancelOrder() {
    showProgress()
    viewModel?.cancelOrder(clientId, orderItem?._id, OrderItem.CancellingEntity.SELLER.name)?.observeOnce(viewLifecycleOwner, Observer {cancelRes->
      hideProgress()
      if (cancelRes.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (cancelRes.status == 200 || cancelRes.status == 201 || cancelRes.status == 202) {
        val data = cancelRes as? OrderConfirmStatus
        data?.let { d -> showLongToast(d.Message as String?) }
        refreshStatus(OrderSummaryModel.OrderStatus.ORDER_CANCELLED)
      } else showLongToast(cancelRes.message())
    })
  }


  private fun refreshStatus(statusOrder: OrderSummaryModel.OrderStatus) {
    isRefresh = true
    orderItem?.Status = statusOrder.name
    orderItem?.let { binding?.orderType?.text = getStatusText(it) }
    orderItem?.let { checkStatusConsultation(it) }
  }

  private fun getStatusText(order: OrderItem): String? {
    val statusValue = OrderStatusValue.fromStatusConsultation(order.status())?.value
    return when {
      OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT) -> {
        return if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
          OrderStatusValue.ESCALATED_3.value
        } else statusValue.plus(order.cancelledTextVideo())
      }
      order.isConfirmConsultBtn() -> "Upcoming Consult"
      else -> statusValue
    }
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
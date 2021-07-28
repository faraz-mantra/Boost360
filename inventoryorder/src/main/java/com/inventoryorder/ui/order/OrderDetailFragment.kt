package com.inventoryorder.ui.order

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentOrderDetailBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.bottomsheet.DeliveryModel
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.rest.response.order.ProductResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.appointmentSpa.sheetAptSpa.SendFeedbackAptSheetDialog
import com.inventoryorder.ui.appointmentSpa.sheetAptSpa.SendReBookingAptSheetDialog
import com.inventoryorder.ui.order.createorder.SendFeedbackOrderSheetDialog
import com.inventoryorder.ui.order.createorder.SendReBookingOrderSheetDialog
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class OrderDetailFragment : BaseInventoryFragment<FragmentOrderDetailBinding>() {

  private var deliverySheetDialog: DeliveryBottomSheetDialog? = null
  private var orderItem: OrderItem? = null
  private var deliveryList = DeliveryModel().getData()
  private var isRefresh: Boolean = false
  private var productList: ArrayList<ProductResponse>? = null
  private var bottomButtonStatus: List<OrderMenuModel.MenuStatus>? = null

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
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let {
      showProgress()
      apiGetOrderDetails(it, "")
    }
    setOnClickListener(
      binding?.tvCustomerContactNumber,
      binding?.tvCustomerEmail
    ) //binding?.btnPickUp,
  }

  private fun apiGetOrderDetails(orderId: String, message: String) {
    viewModel?.getOrderDetails(clientId, orderId)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.isSuccess()) {
        orderItem = (it as? OrderDetailResponse)?.Data
        if (orderItem != null) {
          getProductAllDetails()
          if (message.isNotEmpty()) {
            isRefresh = true
            showShortToast(message)
          }
        } else errorUi("Order detail empty.")
      } else errorUi(it.message())
    })
  }

  private fun getProductAllDetails() {
    productList = ArrayList()
    var count = 0
    if (orderItem?.Items.isNullOrEmpty().not()) {
      orderItem?.Items?.forEach {
        viewModel?.getProductDetails(it.Product?._id)
          ?.observeOnce(viewLifecycleOwner, Observer { it1 ->
            count += 1
            val product = it1 as? ProductResponse
            if (count == orderItem?.Items?.size) {
              product?.let { it2 -> productList?.add(it2) }
              addProductToOrder()
            } else product?.let { it2 -> productList?.add(it2) }
          })
      }
    } else addProductToOrder()
  }

  private fun addProductToOrder() {
    productList?.forEach {
      orderItem?.Items?.firstOrNull { it1 -> it1.Product?._id?.trim() == it.Product?._id?.trim() }?.product_detail =
        it.Product
    }
    hideProgress()
    binding?.mainView?.visible()
    binding?.error?.gone()
    setDetails(orderItem!!)
  }

  private fun errorUi(message: String) {
    hideProgress()
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("${getString(R.string.orders)}: #${order.ReferenceNumber}")
    checkStatusOrder(order)
    setOrderDetails(order)
    order.Items?.let { setAdapter(it) }
  }

  private fun checkStatusOrder(order: OrderItem) {
    val btnStatusMenu = order.orderBtnStatus()
    val requestPayment =
      btnStatusMenu.firstOrNull { it == OrderMenuModel.MenuStatus.REQUEST_PAYMENT }
    val cancelOrder = btnStatusMenu.firstOrNull { it == OrderMenuModel.MenuStatus.CANCEL_ORDER }
    bottomButtonStatus =
      btnStatusMenu.filter { (it == OrderMenuModel.MenuStatus.CANCEL_ORDER || it == OrderMenuModel.MenuStatus.REQUEST_PAYMENT).not() }
    if (cancelOrder != null) {
      binding?.tvCancelOrder?.visible()
      binding?.tvCancelOrder?.text = cancelOrder.title
      binding?.tvCancelOrder?.setOnClickListener(this)
    } else {
      binding?.tvCancelOrder?.gone()
    }
    if (requestPayment != null) {
      binding?.btnSendPaymentLink?.visible()
      binding?.viewLine1?.visible()
      binding?.btnSendPaymentLink?.text = requestPayment.title
      binding?.btnSendPaymentLink?.setOnClickListener(this)
    } else {
      binding?.btnSendPaymentLink?.gone()
      binding?.viewLine1?.gone()
    }
    if (bottomButtonStatus.isNullOrEmpty().not()) {
      binding?.buttonBottom?.visible()
      binding?.buttonBottom?.setOnClickListener(this)
      binding?.buttonBottom?.text = bottomButtonStatus!!.first().title
    } else {
      binding?.buttonBottom?.gone()
    }
  }

  private fun setAdapter(orderItems: ArrayList<ItemN>) {
    binding?.recyclerViewOrderDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewOrderDetails?.adapter = adapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    menu.findItem(R.id.menu_item_invoice)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_item_invoice -> {
        startFragmentOrderActivity(
          FragmentType.ORDER_INVOICE_VIEW,
          Bundle().apply { putString(INVOICE_URL, orderItem?.getInvoiceUrl() ?: "") })
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  }

  fun getBundleData(): Bundle {
    return Bundle().apply { putBoolean(IntentConstant.IS_REFRESH.name, isRefresh) }
  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = getStatusText(order)
    OrderStatusValue.fromStatusOrder(order.status())?.icon?.let {
      binding?.statusIcon?.setImageResource(
        it
      )
    }
    binding?.tvOrderStatus?.text = order.PaymentDetails?.statusValue()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.methodValue()
    binding?.tvDeliveryType?.text = order.deliveryType()
    binding?.tvItemCount?.visibility =
      if (order.Items.isNullOrEmpty().not()) View.VISIBLE else View.GONE
    binding?.tvItemCount?.text =
      if (order.Items?.size ?: 0 > 1) "(${order.Items?.size} items)" else "(${order.Items?.size} item)"

    order.BillingDetails?.let { bill ->
      val currency =
        takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() }
          ?: "INR"
      binding?.tvOrderAmount?.text =
        "$currency ${DecimalFormat("##,##,##0").format(bill.AmountPayableByBuyer)}"
      //binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding?.orderDate?.text = DateUtils.parseDate(
      order.UpdatedOn,
      FORMAT_SERVER_DATE,
      FORMAT_SERVER_TO_LOCAL_2,
      timeZone = TimeZone.getTimeZone("IST")
    )

    // customer details
    binding?.tvCustomerName?.text = order.BuyerDetails?.ContactDetails?.FullName?.trim()
    binding?.tvCustomerDetail?.text = order.BuyerDetails?.getPhoneEmailFull()
    binding?.userAddress?.tvShippingAddress?.text =
      "${order.BuyerDetails?.address()?.addressLine1()} ${order.BuyerDetails?.address()?.Zipcode}"
    binding?.userAddress?.tvBillingAddress?.text =
      "${order.BuyerDetails?.address()?.addressLine1()} ${order.BuyerDetails?.address()?.Zipcode}"

//        binding?.tvCustomerContactNumber?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerContactNumber?.setPaintFlags(it) }
//        binding?.tvCustomerEmail?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerEmail?.setPaintFlags(it) }
//        binding?.tvCustomerContactNumber?.text = order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()

    if (order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()?.let { !checkValidMobile(it) } == true)
      binding?.tvCustomerContactNumber?.setTextColor(getColor(R.color.watermelon_light_10))
    if (order.BuyerDetails?.ContactDetails?.EmailId.isNullOrEmpty().not()) {
      //binding?.tvCustomerEmail?.text = order.BuyerDetails.ContactDetails.EmailId?.trim()
      if (!checkValidEmail(order.BuyerDetails?.ContactDetails?.EmailId!!.trim())) binding?.tvCustomerEmail?.setTextColor(getColor(R.color.watermelon_light_10))
    } else binding?.tvCustomerEmail?.isGone = true


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
    binding?.tvShippingCost?.text =
      "Shipping Cost: $currency ${DecimalFormat("##,##,##0").format(shippingCost)}"
    binding?.tvTotalOrderAmount?.text =
      "Total Amount: $currency ${DecimalFormat("##,##,##0").format(salePrice + shippingCost)}"
//        binding?.tvShippingCost?.text = "Shipping Cost: $currency $shippingCost"
//        binding?.tvTotalOrderAmount?.text = "Total Amount: $currency ${salePrice + shippingCost}"

  }

  private fun getStatusText(order: OrderItem): String? {
    val statusValue = OrderStatusValue.fromStatusOrder(order.status())?.value
    return if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status()
        .toUpperCase(Locale.ROOT)
    ) {
      statusValue.plus(order.cancelledText())
    } else statusValue
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonBottom -> if (bottomButtonStatus.isNullOrEmpty()
          .not()
      ) orderItem?.let { clickActionOrderButton(bottomButtonStatus!!.first(), it) }
      binding?.btnSendPaymentLink -> orderItem?.let {
        clickActionOrderButton(
          OrderMenuModel.MenuStatus.REQUEST_PAYMENT,
          it
        )
      }
      binding?.tvCancelOrder -> orderItem?.let {
        clickActionOrderButton(
          OrderMenuModel.MenuStatus.CANCEL_ORDER,
          it
        )
      }
      binding?.tvCustomerContactNumber -> {
        if (orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()
            ?.let { checkValidMobile(it) }!!
        ) openDialer()
        else showShortToast(getString(R.string.phone_invalid_format_error))
      }
      binding?.tvCustomerEmail -> {
        if (orderItem?.BuyerDetails?.ContactDetails?.EmailId?.trim()
            ?.let { checkValidEmail(it) }!!
        ) openEmailApp()
        else showShortToast(getString(R.string.email_invalid_format_error))
      }
    }
  }

  private fun clickActionOrderButton(orderMenu: OrderMenuModel.MenuStatus, orderItem: OrderItem) {
    when (orderMenu) {
      OrderMenuModel.MenuStatus.CONFIRM_ORDER -> {
        val sheetConfirm = ConfirmBottomSheetDialog()
        sheetConfirm.setData(orderItem)
        sheetConfirm.onClicked = { apiConfirmOrder(it) }
        sheetConfirm.show(this.parentFragmentManager, ConfirmBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
        val sheetRequestPayment = RequestPaymentBottomSheetDialog()
        sheetRequestPayment.setData(orderItem)
        sheetRequestPayment.onClicked = {
          showProgress()
          sendPaymentLinkOrder(getString(R.string.payment_request_send))
        }
        sheetRequestPayment.show(this.parentFragmentManager, RequestPaymentBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.CANCEL_ORDER -> {
        val sheetCancel = CancelBottomSheetDialog()
        sheetCancel.setData(orderItem)
        sheetCancel.onClicked = this@OrderDetailFragment::apiCancelOrder
        sheetCancel.show(this.parentFragmentManager, CancelBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> markCodPaymentRequest()
      OrderMenuModel.MenuStatus.MARK_AS_DELIVERED -> {
        val sheetDelivered = DeliveredBottomSheetDialog()
        sheetDelivered.setData(orderItem)
        sheetDelivered.onClicked = { s, b -> deliveredOrder(s, b) }
        sheetDelivered.show(this.parentFragmentManager, DeliveredBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_AS_SHIPPED -> {
        val sheetShipped = ShippedBottomSheetDialog()
        sheetShipped.setData(orderItem)
        sheetShipped.onClicked = { shippedOrder(it) }
        sheetShipped.show(this.parentFragmentManager, ShippedBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.SEND_RE_BOOKING -> {
        val sheetReBookingApt = SendReBookingOrderSheetDialog()
        sheetReBookingApt.setData(orderItem)
        sheetReBookingApt.onClicked = { sendReBookingRequestOrder() }
        sheetReBookingApt.show(this.parentFragmentManager, SendReBookingAptSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_FEEDBACK -> {
        val sheetFeedbackApt = SendFeedbackOrderSheetDialog()
        sheetFeedbackApt.setData(orderItem)
        sheetFeedbackApt.onClicked = { sendFeedbackRequestOrder(it, getString(R.string.order_feedback_requested)) }
        sheetFeedbackApt.show(this.parentFragmentManager, SendFeedbackAptSheetDialog::class.java.name)
      }
      else -> {
      }
    }
  }


  private fun openEmailApp() {
    val emailIntent = Intent(
      Intent.ACTION_SENDTO,
      Uri.fromParts("mailto", orderItem?.BuyerDetails?.ContactDetails?.EmailId?.trim(), null)
    )
    startActivity(emailIntent)
  }

  private fun openDialer() {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data =
      (Uri.parse("tel:${orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()}"))
    startActivity(intent)
  }

  private fun checkValidEmail(email: String): Boolean {
    return Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matcher(email).find()
  }

  private fun checkValidMobile(mobile: String): Boolean {
    return Pattern.compile("^[+]?[0-9]{10,12}\$").matcher(mobile).find()
  }

  private fun shippedOrder(markAsShippedRequest: MarkAsShippedRequest) {
    showProgress()
    viewModel?.markAsShipped(clientId, markAsShippedRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        orderItem?._id?.let { it1 ->
          apiGetOrderDetails(
            it1,
            resources.getString(R.string.order_shipped)
          )
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun deliveredOrder(message: String, feedback: Boolean) {
    showProgress()
    viewModel?.markAsDelivered(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (feedback) sendFeedbackRequestOrder(FeedbackRequest(orderItem?._id, message))
        orderItem?._id?.let { it1 ->
          apiGetOrderDetails(
            it1,
            resources.getString(R.string.order_delivery)
          )
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiCancelOrder(cancellingEntity: String, reasonText: String) {
    showProgress()
    viewModel?.cancelOrder(clientId, this.orderItem?._id, cancellingEntity)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          val data = it as? OrderConfirmStatus
          if (reasonText.isNotEmpty()) {
            updateReason(
              resources.getString(R.string.order_cancel),
              UpdateExtraPropertyRequest.PropertyType.CANCELLATION.name,
              ExtraPropertiesOrder(cancellationRemark = reasonText)
            )
          } else orderItem?._id?.let { it1 ->
            apiGetOrderDetails(
              it1,
              resources.getString(R.string.order_cancel)
            )
          }
        } else {
          showLongToast(it.message())
          hideProgress()
        }
      })
  }

  private fun updateReason(
    message: String,
    type: String,
    extraPropertiesOrder: ExtraPropertiesOrder
  ) {
    val propertyRequest = UpdateOrderNPropertyRequest(
      updateExtraPropertyType = type,
      existingKeyName = "",
      orderId = this.orderItem?._id,
      extraPropertiesOrder = extraPropertiesOrder
    )
    viewModel?.updateExtraPropertyOrder(clientId, requestCancel = propertyRequest)
      ?.observeOnce(viewLifecycleOwner, {
        orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, message) }
      })
  }

  private fun markCodPaymentRequest() {
    showProgress()
    viewModel?.markCodPaymentDone(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        orderItem?._id?.let { it1 ->
          apiGetOrderDetails(
            it1,
            getString(R.string.payment_confirmed)
          )
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiConfirmOrder(isSendPaymentLink: Boolean) {
    showProgress()
    viewModel?.confirmOrder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (isSendPaymentLink) sendPaymentLinkOrder(getString(R.string.order_confirmed))
        else orderItem?._id?.let { it1 ->
          apiGetOrderDetails(
            it1,
            getString(R.string.order_confirmed)
          )
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendFeedbackRequestOrder(request: FeedbackRequest, message: String? = null) {
    showProgress()
    viewModel?.sendOrderFeedbackRequest(clientId, request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        message?.let { it1 -> orderItem?._id?.let { it2 -> apiGetOrderDetails(it2, it1) } }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendReBookingRequestOrder() {
    showProgress()
    viewModel?.sendReBookingReminder(clientId, this.orderItem?._id)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          orderItem?._id?.let { it1 ->
            apiGetOrderDetails(
              it1,
              getString(R.string.re_booking_reminder)
            )
          }
        } else {
          showLongToast(it.message())
          hideProgress()
        }
      })
  }

  private fun sendPaymentLinkOrder(message: String) {
    viewModel?.sendPaymentReminder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, message) }
    })
  }
}
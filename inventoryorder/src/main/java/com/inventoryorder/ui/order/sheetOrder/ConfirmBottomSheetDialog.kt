package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetConfirmOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN

class ConfirmBottomSheetDialog : BaseBottomSheetDialog<BottomSheetConfirmOrderBinding, BaseViewModel>() {

  var isCheckLink: Boolean = false
  var onClicked: () -> Unit = {}
  private var orderItem: OrderItem? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_confirm_order
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.tvSubTitle?.text = "Order ID #${orderItem?.ReferenceNumber ?: ""}"
    val method = PaymentDetailsN.METHOD.fromType(orderItem?.PaymentDetails?.method())
    val statusPayment = PaymentDetailsN.STATUS.from(orderItem?.PaymentDetails?.status())
    val isPaymentDone = (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))
    binding?.txtDeliveryMode?.text = fromHtml("Delivery mode: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.deliveryType()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    binding?.txtPaymentMode?.text = fromHtml("Payment mode: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.PaymentDetails?.methodValue()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    binding?.txtPaymentStatus?.text = fromHtml("Payment status: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.PaymentDetails?.status()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    if (isPaymentDone.not()) {
      checkUi()
      binding?.btnCheckOnlineLink?.setOnClickListener {
        isCheckLink = !isCheckLink
        checkUi()
      }
    } else {
      binding?.line?.gone()
      binding?.onlinePaymentLinkCheck?.gone()
      binding?.btnCheckOnlineLink?.gone()
      binding?.btnVertical?.gone()
      binding?.btnHorizontal?.visible()
      binding?.txtNote?.text = "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)}."
    }
  }

  private fun checkUi() {
    if (isCheckLink) {
      binding?.onlinePaymentLinkCheck?.visible()
      binding?.btnVertical?.visible()
      binding?.btnHorizontal?.gone()
      binding?.line?.gone()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order_online_link_check, 0, 0, 0)
      binding?.txtNote?.text = "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)} ${getString(R.string.boost_along_payment_order)}"
    } else {
      binding?.onlinePaymentLinkCheck?.gone()
      binding?.btnVertical?.gone()
      binding?.btnHorizontal?.visible()
      binding?.line?.visible()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order_online_link_uncheck, 0, 0, 0)
      binding?.txtNote?.text = "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)}."

    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> {
      }
    }
  }
}
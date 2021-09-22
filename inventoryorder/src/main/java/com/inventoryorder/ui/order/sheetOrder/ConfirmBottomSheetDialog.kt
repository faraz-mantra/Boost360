package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import androidx.core.content.ContextCompat
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetConfirmOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN

class ConfirmBottomSheetDialog :
  BaseBottomSheetDialog<BottomSheetConfirmOrderBinding, BaseViewModel>() {

  var isCheckLink: Boolean = true
  var onClicked: (isCheckLink: Boolean) -> Unit = {}
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
    setOnClickListener(
      binding?.buttonDone,
      binding?.buttonDoneN,
      binding?.tvCancel,
      binding?.tvCancelN
    )
    binding?.tvSubTitle?.text = "Order ID #${orderItem?.ReferenceNumber ?: ""}"
    val method = PaymentDetailsN.METHOD.fromType(orderItem?.PaymentDetails?.method())
    val statusPayment = PaymentDetailsN.STATUS.from(orderItem?.PaymentDetails?.status())
    val number = orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
    val email = orderItem?.BuyerDetails?.ContactDetails?.EmailId
    if (number.isNullOrEmpty().not()) {
      binding?.checkboxSms?.visible()
      binding?.checkboxSms?.text = "via SMS ($number)"
    } else binding?.checkboxSms?.gone()

    if (email.isNullOrEmpty().not()) {
      binding?.checkboxEmail?.visible()
      binding?.checkboxEmail?.text = "via Email ($email)"
    } else binding?.checkboxEmail?.gone()


    val isPaymentDone =
      (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))
    binding?.txtDeliveryMode?.text =
      fromHtml("Delivery mode: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.deliveryType()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    binding?.txtPaymentMode?.text =
      fromHtml("Payment mode: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.PaymentDetails?.methodValue()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    binding?.txtPaymentStatus?.text =
      fromHtml("Payment status: ${takeIf { isPaymentDone.not() }?.let { "<b>" } ?: ""}${orderItem?.PaymentDetails?.status()}${takeIf { isPaymentDone.not() }?.let { "</b>" } ?: ""}")
    val iconPayment = ContextCompat.getDrawable(
      baseActivity,
      if (isPaymentDone) R.drawable.ic_order_status_success else R.drawable.ic_order_status_pending
    )
    binding?.txtPaymentStatus?.setCompoundDrawablesWithIntrinsicBounds(
      iconPayment,
      null,
      null,
      null
    )
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
      binding?.txtNote?.text =
        "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)}."
    }
  }

  private fun checkUi() {
    if (isCheckLink) {
      binding?.onlinePaymentLinkCheck?.visible()
      binding?.btnVertical?.visible()
      binding?.btnHorizontal?.gone()
      binding?.line?.gone()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.ic_order_online_link_check,
        0,
        0,
        0
      )
      binding?.txtNote?.text =
        "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)} ${
          getString(R.string.boost_along_payment_order)
        }"
    } else {
      binding?.onlinePaymentLinkCheck?.gone()
      binding?.btnVertical?.gone()
      binding?.btnHorizontal?.visible()
      binding?.line?.visible()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.ic_order_online_link_uncheck,
        0,
        0,
        0
      )
      binding?.txtNote?.text =
        "${getString(R.string.boost_will_send_an_email_and_an_sms_confirmation_of_this_order)}."

    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked(false)
      binding?.buttonDoneN -> onClicked(isCheckLink)
    }
  }
}
package com.inventoryorder.ui.appointmentSpa.sheetAptSpa

import android.view.View
import androidx.core.content.ContextCompat
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetConfirmAptBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN

class ConfirmAptSheetDialog : BaseBottomSheetDialog<BottomSheetConfirmAptBinding, BaseViewModel>() {

  var isCheckLink: Boolean = true
  var onClicked: (isCheckLink: Boolean) -> Unit = {}
  private var orderItem: OrderItem? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_confirm_apt
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
    binding?.tvSubTitle?.text = "Appointment ID #${orderItem?.ReferenceNumber ?: ""}"
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


    binding?.txtSymbol?.text = orderItem?.BillingDetails?.getCurrencyCodeValue() ?: "INR"
    binding?.txtPrice?.text = "${orderItem?.BillingDetails?.AmountPayableByBuyer ?: 0.0}"
    val isPaymentDone = (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))
    binding?.txtDeliveryMode?.text = fromHtml("Service Location: <b>Business</b>")
    binding?.txtPaymentMode?.text = fromHtml("Payment mode: <b>${orderItem?.PaymentDetails?.methodValue()}</b>")
    binding?.txtPaymentStatus?.text = fromHtml("Payment status: <b>${orderItem?.PaymentDetails?.status()}</b>")
    val iconPayment = ContextCompat.getDrawable(baseActivity, if (isPaymentDone) R.drawable.ic_order_status_success else R.drawable.ic_order_status_pending)
    binding?.txtPaymentStatus?.setCompoundDrawablesWithIntrinsicBounds(iconPayment, null, null, null)
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
        "${getString(R.string.send_an_email_and_an_sms_confirmation_of_this_apt)}."
    }
  }

  private fun checkUi() {
    if (isCheckLink) {
      binding?.onlinePaymentLinkCheck?.visible()
      binding?.btnVertical?.visible()
      binding?.btnHorizontal?.gone()
      binding?.line?.gone()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order_online_link_check, 0, 0, 0)
      binding?.txtNote?.text = "${getString(R.string.send_an_email_and_an_sms_confirmation_of_this_apt)} ${getString(R.string.boost_along_payment_order)}"
    } else {
      binding?.onlinePaymentLinkCheck?.gone()
      binding?.btnVertical?.gone()
      binding?.btnHorizontal?.visible()
      binding?.line?.visible()
      binding?.btnCheckOnlineLink?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order_online_link_uncheck, 0, 0, 0)
      binding?.txtNote?.text = "${getString(R.string.send_an_email_and_an_sms_confirmation_of_this_apt)}."

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
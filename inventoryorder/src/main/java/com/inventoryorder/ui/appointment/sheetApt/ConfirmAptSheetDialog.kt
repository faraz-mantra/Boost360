package com.inventoryorder.ui.appointment.sheetApt

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
import java.math.BigDecimal
import java.text.DecimalFormat

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
    setOnClickListener(binding?.buttonDone, binding?.buttonDoneN, binding?.tvCancel, binding?.tvCancelN)
    binding?.tvSubTitle?.text = "Appointment ID #${orderItem?.ReferenceNumber ?: ""}"
    binding?.txtSymbol?.text = takeIf { orderItem?.BillingDetails?.CurrencyCode.isNullOrEmpty().not() }?.let { orderItem?.BillingDetails?.CurrencyCode?.trim() } ?: "INR"
    binding?.txtPrice?.text = "${DecimalFormat("##,##,##0.00").format(BigDecimal(orderItem?.BillingDetails?.AmountPayableByBuyer ?: 0.0))}"
    val method = PaymentDetailsN.METHOD.fromType(orderItem?.PaymentDetails?.method())
    val statusPayment = PaymentDetailsN.STATUS.from(orderItem?.PaymentDetails?.status())
    val number = orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
    val email = orderItem?.BuyerDetails?.ContactDetails?.EmailId
    if (number.isNullOrEmpty().not()) {
      binding?.checkboxSms?.visible()
      binding?.checkboxSms?.text = fromHtml("via SMS (<u>$number</u>)")
    } else binding?.checkboxSms?.gone()

    if (email.isNullOrEmpty().not()) {
      binding?.checkboxEmail?.visible()
      binding?.checkboxEmail?.text = fromHtml("via Email (<u>$email</u>)")
    } else binding?.checkboxEmail?.gone()


    val isPaymentDone = (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))
    activity?.let { binding?.txtPrice?.setTextColor(ContextCompat.getColor(it, if (isPaymentDone) R.color.green_light_1 else R.color.watermelon_light)) }
    activity?.let { binding?.txtSymbol?.setTextColor(ContextCompat.getColor(it, if (isPaymentDone) R.color.green_light_1 else R.color.watermelon_light)) }
    binding?.txtDeliveryMode?.text = fromHtml("Service location: <b>${orderItem?.SellerDetails?.Address?.City ?: "NA"}</b>")
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
      binding?.buttonDone -> onClicked(false)
      binding?.buttonDoneN -> onClicked(isCheckLink)
    }
  }
}
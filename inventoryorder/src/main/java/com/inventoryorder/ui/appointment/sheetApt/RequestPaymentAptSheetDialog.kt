package com.inventoryorder.ui.appointment.sheetApt

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetRequestPaymentAptBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import java.math.BigDecimal
import java.text.DecimalFormat

class RequestPaymentAptSheetDialog : BaseBottomSheetDialog<BottomSheetRequestPaymentAptBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_request_payment_apt
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    orderItem?.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      val formatAmount = "${DecimalFormat("##,##,##0.00").format(BigDecimal(bill.AmountPayableByBuyer?:0.0))}"
      val ss = SpannableString("$formatAmount")
      ss.setSpan(RelativeSizeSpan(0.5f), "$formatAmount".indexOf("."), "$formatAmount".length, 0)
      binding?.tvSubTitle?.text = "Collect $currency $ss for appointment ID #${orderItem?.ReferenceNumber ?: ""}"
      val service=orderItem?.firstItemForConsultation()
     binding?.txtPriceAllService?.text= fromHtml( "$currency $formatAmount for 1 service in <u>#${orderItem?.ReferenceNumber}</u>")
    }

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
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked()
    }
  }

}
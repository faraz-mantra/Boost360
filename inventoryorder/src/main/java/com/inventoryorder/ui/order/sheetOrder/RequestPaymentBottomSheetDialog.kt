package com.inventoryorder.ui.order.sheetOrder

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetRequestPaymentOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import java.math.BigDecimal
import java.text.DecimalFormat

class RequestPaymentBottomSheetDialog : BaseBottomSheetDialog<BottomSheetRequestPaymentOrderBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_request_payment_order
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
      val formatAmount = "${DecimalFormat("##,##,##0.00").format(BigDecimal(bill.AmountPayableByBuyer!!))}"
      val ss = SpannableString("$formatAmount")
      ss.setSpan(RelativeSizeSpan(0.5f), "$formatAmount".indexOf("."), "$formatAmount".length, 0)
      binding?.tvSubTitle?.text = "Collect $currency $ss for order ID #${orderItem?.ReferenceNumber ?: ""}"
    }

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
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked()
    }
  }

}
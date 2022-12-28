package com.appservice.ui.catalog.widgets

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomShettPaymentConfigurationBinding
import com.appservice.model.accountDetails.BankAccountDetails
import com.appservice.model.serviceProduct.CatalogProduct
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class PaymentConfigBottomSheet : BaseBottomSheetDialog<BottomShettPaymentConfigurationBinding, BaseViewModel>() {

  private var bankAccountDetail: BankAccountDetails? = null
  private var paymentType: String? = null
  var onClicked: (paymentType: String?) -> Unit = { }
  var onListenerChange: () -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_shett_payment_configuration
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setDataPaymentGateway(bankAccountDetail: BankAccountDetails?, paymentType: String?) {
    this.bankAccountDetail = bankAccountDetail
    this.paymentType = paymentType
  }

  override fun onCreateView() {
    setStringSpannableConfigs()
    setOnClickListener(binding?.vwBoostPaymentGateway, binding?.vwExternalUrl, binding?.changeBankDetail)
    setOnClickListener(binding?.btnDone, binding?.btnCancel)
    binding?.changeBankDetail?.text = resources.getString(if (bankAccountDetail != null) R.string.update_bank_detail else R.string.add_bank_account)
    binding?.rbBoostPaymentGateway?.isChecked = (paymentType == CatalogProduct.PaymentType.ASSURED_PURCHASE.value)
    binding?.rbExternalUrl?.isChecked = (paymentType == CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value)
  }

  private fun setStringSpannableConfigs() {
    val content = SpannableString(getString(R.string.a_premium_service_by_boost_for_secure_payment_collection_learn_more_here))
    val clickableSpanHereLink: ClickableSpan = object : ClickableSpan() {
      override fun onClick(p0: View) {
        showShortToast(getString(R.string.coming_soon))
      }
    }
    val clickableSpanNotHereLink: ClickableSpan = object : ClickableSpan() {
      override fun onClick(p0: View) {
        boostPaymentGatewayClicked()
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
      }
    }
    content.setSpan(clickableSpanHereLink, content.indexOf("here"), content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    content.setSpan(clickableSpanNotHereLink, 0, content.indexOf("here").minus(1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    binding?.tvBoostPaymentGatewayDesc?.movementMethod = LinkMovementMethod.getInstance()
    binding?.tvBoostPaymentGatewayDesc?.text = content
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwBoostPaymentGateway -> {
        boostPaymentGatewayClicked()
      }
      binding?.vwExternalUrl -> {
        paymentType = CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value
        binding?.rbBoostPaymentGateway?.isChecked = false
        binding?.rbExternalUrl?.isChecked = binding?.rbExternalUrl?.isChecked?.not() ?: false
      }
      binding?.changeBankDetail -> {
        onListenerChange()
        dismiss()
      }
      binding?.btnDone -> {
        onClicked(paymentType)
        dismiss()
      }
      binding?.btnCancel -> dismiss()
    }
  }

  private fun boostPaymentGatewayClicked() {
    if (bankAccountDetail != null) {
      paymentType = CatalogProduct.PaymentType.ASSURED_PURCHASE.value
      binding?.rbExternalUrl?.isChecked = false
      binding?.rbBoostPaymentGateway?.isChecked = binding?.rbBoostPaymentGateway?.isChecked?.not() ?: false
    } else showLongToast(getString(R.string.payment_gateway_not_added_please_add_first))
  }
}
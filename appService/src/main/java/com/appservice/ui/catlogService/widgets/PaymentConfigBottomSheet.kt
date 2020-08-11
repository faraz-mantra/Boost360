package com.appservice.ui.catlogService.widgets

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomShettPaymentConfigurationBinding
import com.appservice.model.kycData.DataKyc
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class PaymentConfigBottomSheet : BaseBottomSheetDialog<BottomShettPaymentConfigurationBinding, BaseViewModel>() {

  private var dataKyc: DataKyc? = null
  private var addPaymentGateway: Boolean? = null
  var onClicked: (isAddPaymentGateway: Boolean) -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_shett_payment_configuration
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setDataPaymentGateway(dataKyc: DataKyc?, addPaymentGateway: Boolean?) {
    this.dataKyc = dataKyc
    this.addPaymentGateway = addPaymentGateway
  }

  override fun onCreateView() {
    val content = SpannableString(getString(R.string.a_premium_service_by_boost_for_secure_payment_collection_learn_more_here))
    content.setSpan(UnderlineSpan(), content.indexOf("here"), content.length, 0)
    binding?.tvBoostPaymentGatewayDesc?.text = content
    setOnClickListener(binding?.vwBoostPaymentGateway, binding?.vwExternalUrl)
    setOnClickListener(binding?.btnDone, binding?.btnCancel)
    binding?.rbBoostPaymentGateway?.isChecked = addPaymentGateway ?: false
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwBoostPaymentGateway -> {
        if (dataKyc != null) {
          binding?.rbBoostPaymentGateway?.isChecked = binding?.rbBoostPaymentGateway?.isChecked?.not() ?: false
        } else showLongToast("Boost Payment gateway not added.")
      }
      binding?.btnDone -> {
        onClicked(binding?.rbBoostPaymentGateway?.isChecked ?: false)
        dismiss()
      }
      binding?.btnCancel -> dismiss()
    }
  }

}
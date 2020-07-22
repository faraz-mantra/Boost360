package com.appservice.ui.paymentgateway

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentBinding
import com.framework.models.BaseViewModel

class PaymentGatewayFragment : AppBaseFragment<FragmentPaymentBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): PaymentGatewayFragment {
      val fragment = PaymentGatewayFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_payment
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}

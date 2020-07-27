package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentNotActiveBinding
import com.framework.models.BaseViewModel

class InactivePaymentGatewayFragment : AppBaseFragment<FragmentPaymentNotActiveBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InactivePaymentGatewayFragment {
      val fragment = InactivePaymentGatewayFragment()
      fragment.arguments = bundle
      return fragment
    }
  }


  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(
            binding?.inactivePaymentGatewayTermsToggle
    )
  }

  override fun getLayout(): Int {
    return R.layout.fragment_payment_not_active
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v){
      binding?.inactivePaymentGatewayTermsToggle ->  bottomSheetWhy()
    }
  }

  private fun bottomSheetWhy(){
    WhyPaymentBottomSheet().show(this@InactivePaymentGatewayFragment.parentFragmentManager, com.appservice.ui.bankaccount.WhyBottomSheet::class.java.name)
  }


}

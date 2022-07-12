package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentBusinessKycBinding
import com.appservice.model.SessionData
import com.framework.models.BaseViewModel

@Deprecated(message = "new screen: com.appservice.ui.businessVerification.BusinessVerificationFragment")
class BusinessKycFragment : AppBaseFragment<FragmentBusinessKycBinding, BaseViewModel>() {

  private var session: SessionData? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BusinessKycFragment {
      val fragment = BusinessKycFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_business_kyc
  }

  override fun onCreateView() {
    super.onCreateView()
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData ?: return
    setOnClickListener(binding?.btnStartPanCard)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnStartPanCard -> {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
        startFragmentPaymentActivity(FragmentType.SCAN_PAN_CARD, bundle)
      }
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}

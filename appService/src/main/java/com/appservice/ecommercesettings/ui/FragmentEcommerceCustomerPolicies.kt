package com.appservice.ecommercesettings.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.base.startWebViewPageLoad
import com.appservice.databinding.FragmentEcommercePoliciesForCustomersBinding
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class FragmentEcommerceCustomerPolicies : AppBaseFragment<FragmentEcommercePoliciesForCustomersBinding, BaseViewModel>() {

  companion object {
    fun newInstance(): FragmentEcommerceCustomerPolicies {
      return FragmentEcommerceCustomerPolicies()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_ecommerce_policies_for_customers
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.privacyPolicy)
    sessionLocal = UserSessionManager(requireActivity());
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.privacyPolicy -> {
        openPrivacyPolicy()
      }
    }
  }

  private fun openPrivacyPolicy() {
    baseActivity.startWebViewPageLoad(sessionLocal, "https://www.getboost360.com/privacy");
  }

}
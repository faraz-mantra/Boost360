package com.appservice.ui.aptsetting.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.base.startWebViewPageLoad
import com.appservice.databinding.FragmentPoliciesForCustomerBinding
import com.framework.models.BaseViewModel

class FragmentCustomerPolicies : AppBaseFragment<FragmentPoliciesForCustomerBinding, BaseViewModel>() {
  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.fragment_policies_for_customer
  }

  companion object {
    fun newInstance(): FragmentCustomerPolicies {
      return FragmentCustomerPolicies()
    }
  }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.privacyPolicy)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.privacyPolicy->{
              openPrivacyPolicy()
            }
        }
    }

  private fun openPrivacyPolicy() {
    baseActivity.startWebViewPageLoad(sessionLocal, "https://www.getboost360.com/privacy");
  }
}
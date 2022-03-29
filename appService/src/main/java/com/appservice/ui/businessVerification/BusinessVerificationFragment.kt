package com.appservice.ui.businessVerification

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBusinessVerificationBinding
import com.framework.models.BaseViewModel

class BusinessVerificationFragment : AppBaseFragment<FragmentBusinessVerificationBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_business_verification
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
}
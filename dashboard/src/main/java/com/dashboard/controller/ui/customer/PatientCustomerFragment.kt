package com.dashboard.controller.ui.customer

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentPatientsCustomerBinding
import com.framework.models.BaseViewModel

class PatientCustomerFragment : AppBaseFragment<FragmentPatientsCustomerBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_patients_customer
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}
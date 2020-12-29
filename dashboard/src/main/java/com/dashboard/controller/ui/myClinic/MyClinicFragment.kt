package com.dashboard.controller.ui.myClinic

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentPatientsBinding
import com.framework.models.BaseViewModel

class MyClinicFragment : AppBaseFragment<FragmentPatientsBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_patients
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}
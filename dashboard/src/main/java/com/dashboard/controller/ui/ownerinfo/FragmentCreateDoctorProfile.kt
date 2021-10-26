package com.dashboard.controller.ui.ownerinfo

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAddDoctorBinding
import com.dashboard.viewmodel.DashboardViewModel

class FragmentCreateDoctorProfile : AppBaseFragment<FragmentAddDoctorBinding, DashboardViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_add_doctor
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): FragmentCreateDoctorProfile {
      val fragment = FragmentCreateDoctorProfile()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }
}
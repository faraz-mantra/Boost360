package com.appservice.staffs.doctors

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAdditionalDoctorInfoBinding
import com.appservice.staffs.ui.viewmodel.StaffViewModel

class AdditionalDoctorsInfoFragment : AppBaseFragment<FragmentAdditionalDoctorInfoBinding, StaffViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_additional_doctor_info
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }
  companion object{
    fun newInstance(): AdditionalDoctorsInfoFragment {
      return AdditionalDoctorsInfoFragment()
    }
  }
}
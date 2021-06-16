package com.appservice.staffs.doctors

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentEditDoctorInfoBinding
import com.appservice.staffs.ui.viewmodel.StaffViewModel

class EditDoctorsDetailsFragment :
  AppBaseFragment<FragmentEditDoctorInfoBinding, StaffViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_edit_doctor_info
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  companion object {
    fun newInstance(): EditDoctorsDetailsFragment {
      return EditDoctorsDetailsFragment()
    }
  }
}
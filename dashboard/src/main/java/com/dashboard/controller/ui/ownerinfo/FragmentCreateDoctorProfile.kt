package com.dashboard.controller.ui.ownerinfo

import android.os.Bundle
import com.appservice.utils.changeColorOfSubstring
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


  /**
   * Uncomment this to make all asterisk in UI to colorAccent color
   * */
  /*private fun setupUIColor() {
    changeColorOfSubstring(com.appservice.R.string.name_doctor, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorName!!)
    changeColorOfSubstring(com.appservice.R.string.description, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorDesc!!)
    changeColorOfSubstring(com.appservice.R.string.speciality, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorSpeciality!!)
    changeColorOfSubstring(com.appservice.R.string.business_license, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorLicense!!)
    changeColorOfSubstring(com.appservice.R.string.avg_duration_of_consultaton, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorConsult!!)
    changeColorOfSubstring(com.appservice.R.string.appointment_booking_window_for_patients, com.appservice.R.color.colorAccent, "*", binding?.tvDoctorBookingWindow!!)
  }*/
}
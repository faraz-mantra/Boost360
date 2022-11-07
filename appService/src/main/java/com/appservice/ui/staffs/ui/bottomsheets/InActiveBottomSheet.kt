package com.appservice.ui.staffs.ui.bottomsheets

import android.view.KeyEvent
import android.view.View
import com.appservice.R
import com.appservice.base.isDoctorClinicProfile
import com.appservice.databinding.BottomsheetInactiveStaffBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class InActiveBottomSheet : BaseBottomSheetDialog<BottomsheetInactiveStaffBinding, BaseViewModel>() {

  private var value: Boolean = true
  private lateinit var session: UserSessionManager
  private val isDoctor: Boolean
    get() {
      return isDoctorClinicProfile(session.fP_AppExperienceCode)
    }
  var onClicked: (value: Boolean) -> Unit = { }
  var onBackPres: () -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottomsheet_inactive_staff
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(baseActivity)
    binding?.txtStatus?.text = if (isDoctor) getString(R.string.this_doctor_is_marked_inactive) else getString(R.string.this_staff_is_marked_inactive)
    binding?.btnActivateStaff?.text = if (isDoctor) getString(R.string.activate_doctor_profile) else getString(R.string.activate_staff_profile)
    setOnClickListener(binding?.btnActivateStaff)
    dialog.setCanceledOnTouchOutside(false)
    getDialog()?.setOnKeyListener { _, keyCode, _ ->
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        onBackPres()
        true
      } else false
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnActivateStaff -> {
        dismiss()
        onClicked(value)
      }
    }
  }
}
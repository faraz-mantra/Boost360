package com.appservice.ui.staffs.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.base.isDoctorProfile
import com.appservice.databinding.BottomsheetInactiveStaffConfirmationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class InActiveStaffConfirmationBottomSheet : BaseBottomSheetDialog<BottomsheetInactiveStaffConfirmationBinding, BaseViewModel>() {

  private var value: Boolean = true
  private lateinit var session: UserSessionManager
  private val isDoctor: Boolean
    get() {
      return isDoctorProfile(session.fP_AppExperienceCode)
    }
  var onClicked: (value: Boolean) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottomsheet_inactive_staff_confirmation
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(baseActivity)
    binding?.txtTitle?.text = if (isDoctor) getString(R.string.do_you_want_to_temporarily_mark_this_doctor) else getString(R.string.do_you_want_to_temporarily_mark_this_st)
    binding?.ctvAboutStaff?.text = if (isDoctor) getString(R.string.marking_as_inactive_will_hide_doctor) else getString(R.string.marking_as_inactive_will_hide)
    setOnClickListener(binding?.btnDone, binding?.btnCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        onClicked(value)
        dismiss()
      }
      binding?.btnCancel -> {
        dismiss()
      }
    }
  }


}
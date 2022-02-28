package com.appservice.ui.staffs.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.base.isDoctorProfile
import com.appservice.databinding.BottomsheetRemoveStaffBottomSheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class RemoveStaffConfirmationBottomSheet : BaseBottomSheetDialog<BottomsheetRemoveStaffBottomSheetBinding, BaseViewModel>() {

  private var value: Boolean = true
  private lateinit var session: UserSessionManager
  private val isDoctor: Boolean
    get() {
      return isDoctorProfile(session.fP_AppExperienceCode)
    }
  var onClicked: (value: Boolean) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottomsheet_remove_staff_bottom_sheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(baseActivity)
    binding?.ctvHeadingRemoveStaff?.text = if (isDoctor) getString(R.string.delete_this_doctor) else getString(R.string.do_you_want_to_permanently_delete_this_staff_member)
    binding?.ctvAboutStaff?.text = if (isDoctor) getString(R.string.removing_the_doctor_will_remove) else getString(R.string.removing_the_staff_will_remove)
    binding?.btnDone?.text = if (isDoctor) getString(R.string.delete_doctor_text) else getString(R.string.delete_staff)
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
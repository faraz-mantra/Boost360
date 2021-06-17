package com.appservice.staffs.doctors.bottomsheet

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetAppointmentTypeBinding
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.framework.base.BaseBottomSheetDialog

class AppointmentTypeBottomSheet:
  BaseBottomSheetDialog<BottomSheetAppointmentTypeBinding, StaffViewModel>() {
  var onClicked: (appointmentType: Int?) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_appointment_type
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnGoBack,binding?.btnDone)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnDone->{
        onClicked(1)
        dismiss()
      }
      binding?.btnGoBack->{
        dismiss()
      }
    }
  }
}
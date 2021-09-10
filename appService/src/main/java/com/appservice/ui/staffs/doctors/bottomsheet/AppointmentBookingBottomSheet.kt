package com.appservice.ui.staffs.doctors.bottomsheet

import android.view.View
import androidx.core.view.children
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetAppointmentBookingBinding
import com.appservice.model.staffModel.StaffDetailsResult
import com.appservice.ui.staffs.ui.viewmodel.StaffViewModel

import com.framework.base.BaseBottomSheetDialog
import com.framework.views.customViews.CustomRadioButton

class AppointmentBookingBottomSheet :
  BaseBottomSheetDialog<BottomSheetAppointmentBookingBinding, StaffViewModel>() {
  private var data: StaffDetailsResult? = null
  var onClicked: (windowDuration: String?) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_appointment_booking

  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
   this.data = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    val radioButton = binding?.rgBookingWindow?.children?.filter { (it as? CustomRadioButton)?.text == data?.bookingWindow }?.firstOrNull() as?CustomRadioButton
    radioButton?.isChecked = true
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> {
        dismiss()
      }
      binding?.btnSaveChanges -> {
        val radio = (binding?.rgBookingWindow?.findViewById<CustomRadioButton>(binding?.rgBookingWindow?.checkedRadioButtonId!!))
        if (radio!=null)
        onClicked(radio?.text.toString() ?: "")
        dismiss()
      }
    }
  }
}
package com.appservice.ui.staffs.doctors.bottomsheet

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.children
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetAppointmentBookingBinding
import com.appservice.model.staffModel.StaffDetailsResult
import com.appservice.viewmodel.StaffViewModel

import com.framework.base.BaseBottomSheetDialog

class AppointmentBookingBottomSheet : BaseBottomSheetDialog<BottomSheetAppointmentBookingBinding, StaffViewModel>() {

  private var data: StaffDetailsResult? = null
  var onClicked: (windowDuration: String?) -> Unit = { }
  private  val TAG = "AppointmentBookingBotto"
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_appointment_booking

  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
    this.data = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    Log.i(TAG, "onCreateView: ${data?.bookingWindow.toString()}")
    val radioButton = binding?.rgBookingWindow?.children?.filter {
      (it as? AppCompatRadioButton)?.text?.toString()?.replace(" days","") == (data?.bookingWindow?.toString() ?: "")
    }?.firstOrNull() as? AppCompatRadioButton
    radioButton?.isChecked = true
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> {
        dismiss()
      }
      binding?.btnSaveChanges -> {
        val radio = (binding?.rgBookingWindow?.findViewById<AppCompatRadioButton>(binding?.rgBookingWindow?.checkedRadioButtonId!!))
        if (radio != null)
          onClicked(radio.text.toString())
        dismiss()
      }
    }
  }
}
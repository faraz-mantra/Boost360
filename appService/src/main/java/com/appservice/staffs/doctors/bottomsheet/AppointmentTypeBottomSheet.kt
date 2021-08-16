package com.appservice.staffs.doctors.bottomsheet

import android.view.View
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetAppointmentTypeBinding
import com.appservice.staffs.model.StaffDetailsResult
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.utils.cast
import com.framework.base.BaseBottomSheetDialog


class AppointmentTypeBottomSheet :
  BaseBottomSheetDialog<BottomSheetAppointmentTypeBinding, StaffViewModel>() {
  var onClicked: (appointmentType: Int?) -> Unit = { }
  private var data: StaffDetailsResult? = null
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_appointment_type
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnGoBack, binding?.btnDone)
    this.data = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    setPreviousData()
    binding?.llInpersonConsultation?.setOnClickListener {
      binding?.rbInpersonConsultation?.isChecked =
        true;binding?.rbInpersonVideoConsultation?.isChecked =
      false;binding?.rbVideoConsultation?.isChecked = false
    }
    binding?.llInpersonVideoConsultation?.setOnClickListener {
      binding?.rbInpersonVideoConsultation?.isChecked =
        true;binding?.rbInpersonConsultation?.isChecked =
      false;binding?.rbVideoConsultation?.isChecked = false
    }
    binding?.llVideoConsultation?.setOnClickListener {
      binding?.rbVideoConsultation?.isChecked =
        true;binding?.rbInpersonVideoConsultation?.isChecked =
      false;binding?.rbInpersonConsultation?.isChecked = false
    }
  }

  private fun setPreviousData() {
    when (data?.appointmentType) {
      binding?.rbInpersonConsultation?.tag.toString().toIntOrNull() -> {
        binding?.rbInpersonConsultation?.isChecked = true
      }
      binding?.rbVideoConsultation?.tag .toString().toIntOrNull()-> {
        binding?.rbVideoConsultation?.isChecked = true
      }
      binding?.rbInpersonVideoConsultation?.tag.toString().toIntOrNull() -> {
        binding?.rbInpersonVideoConsultation?.isChecked = true
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        onClicked(findCheckedRadio()?.toString()?.toIntOrNull())
        dismiss()
      }
      binding?.btnGoBack -> {
        dismiss()
      }
    }
  }

  private fun findCheckedRadio(): Any? {
    if (binding?.rbInpersonConsultation?.isChecked == true) {
      return binding?.rbInpersonConsultation?.tag
    }
    if (binding?.rbVideoConsultation?.isChecked == true) {
      return binding?.rbVideoConsultation?.tag
    }
    if (binding?.rbInpersonVideoConsultation?.isChecked == true) {
      return binding?.rbInpersonVideoConsultation?.tag

    }
    return 0
  }
}
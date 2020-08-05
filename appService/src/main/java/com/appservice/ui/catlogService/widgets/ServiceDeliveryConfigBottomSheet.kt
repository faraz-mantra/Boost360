package com.appservice.ui.catlogService.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetServiceDeliveryConfigBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ServiceDeliveryConfigBottomSheet : BaseBottomSheetDialog<BottomSheetServiceDeliveryConfigBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_service_delivery_config
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.vwOnlineOnly, binding?.vwInPersonaOnly, binding?.vwBothOnlineInPerson)
    setOnClickListener(binding?.btnDone, binding?.btnCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwOnlineOnly -> {
        binding?.rbOnlineOnly?.isChecked = true
        binding?.rbInPersonaOnly?.isChecked = false
        binding?.rbBothOnlineInPerson?.isChecked = false
      }
      binding?.vwInPersonaOnly -> {
        binding?.rbOnlineOnly?.isChecked = false
        binding?.rbInPersonaOnly?.isChecked = true
        binding?.rbBothOnlineInPerson?.isChecked = false
      }
      binding?.vwBothOnlineInPerson -> {
        binding?.rbOnlineOnly?.isChecked = false
        binding?.rbInPersonaOnly?.isChecked = false
        binding?.rbBothOnlineInPerson?.isChecked = true
      }
      binding?.btnDone -> {
      }
      binding?.btnCancel -> {
        dismiss()
      }
    }
  }

}
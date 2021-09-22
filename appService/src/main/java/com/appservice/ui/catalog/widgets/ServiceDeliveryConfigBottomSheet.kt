package com.appservice.ui.catalog.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetServiceDeliveryConfigBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ServiceDeliveryConfigBottomSheet :
  BaseBottomSheetDialog<BottomSheetServiceDeliveryConfigBinding, BaseViewModel>() {

  var onClicked: (deliveryConfig: Boolean) -> Unit = { }
  var deliveryConfig: Boolean = true
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_service_delivery_config
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  fun isUpdate(deliveryConfig: Boolean) {
    this.deliveryConfig = deliveryConfig
  }

  override fun onCreateView() {
    setOnClickListener(binding?.vwOnlineOnly)
    setOnClickListener(binding?.btnDone, binding?.btnCancel)

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        onClicked(deliveryConfig)
        dismiss()
      }
      binding?.btnCancel -> dismiss()
    }
  }

}
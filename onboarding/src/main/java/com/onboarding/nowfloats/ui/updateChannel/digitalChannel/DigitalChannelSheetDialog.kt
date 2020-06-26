package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.BottomSheetDigitalChannelBinding

class DigitalChannelSheetDialog : BaseBottomSheetDialog<BottomSheetDigitalChannelBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_digital_channel
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  override fun onCreateView() {
    binding?.close?.setOnClickListener { dismiss() }
  }
}
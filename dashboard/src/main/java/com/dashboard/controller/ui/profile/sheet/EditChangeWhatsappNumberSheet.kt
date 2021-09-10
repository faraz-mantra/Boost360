package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetChangeWhatsappNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeWhatsappNumberSheet : BaseBottomSheetDialog<SheetChangeWhatsappNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_change_whatsapp_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}
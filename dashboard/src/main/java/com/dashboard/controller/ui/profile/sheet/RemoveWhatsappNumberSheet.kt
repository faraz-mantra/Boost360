package com.dashboard.controller.ui.profile.sheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.SheetRemoveWhatsappNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class RemoveWhatsappNumberSheet : BaseBottomSheetDialog<SheetRemoveWhatsappNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_remove_whatsapp_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
      setOnClickListener(binding?.btnDontRemove)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnDontRemove->{
        dismiss()
      }
    }
  }
}
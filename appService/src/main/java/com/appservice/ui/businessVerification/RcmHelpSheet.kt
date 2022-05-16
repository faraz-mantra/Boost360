package com.appservice.ui.businessVerification

import android.view.View
import com.appservice.R
import com.appservice.databinding.RcmHelpSheetBinding
import com.appservice.databinding.SheetBusinessVerificationUnderwayBinding
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class RcmHelpSheet : BaseBottomSheetDialog<RcmHelpSheetBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.rcm_help_sheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object{
    fun newInstance():RcmHelpSheet{
      return RcmHelpSheet()
    }
  }
  override fun onCreateView() {
    setOnClickListener(binding?.btnSubmitSubmit)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnSubmitSubmit->{
        dismiss()
      }
    }
  }
}
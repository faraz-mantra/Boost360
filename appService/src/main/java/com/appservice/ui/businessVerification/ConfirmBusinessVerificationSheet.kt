package com.appservice.ui.businessVerification

import com.appservice.R
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ConfirmBusinessVerificationSheet : BaseBottomSheetDialog<SheetConfirmBusinessVerificationBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_confirm_business_verification
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}
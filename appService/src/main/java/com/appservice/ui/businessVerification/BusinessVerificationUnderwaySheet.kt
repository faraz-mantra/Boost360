package com.appservice.ui.businessVerification

import com.appservice.R
import com.appservice.databinding.SheetBusinessVerificationUnderwayBinding
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessVerificationUnderwaySheet : BaseBottomSheetDialog<SheetBusinessVerificationUnderwayBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_business_verification_underway
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}
package com.appservice.ui.businessVerification

import android.view.View
import com.appservice.R
import com.appservice.databinding.SheetBusinessVerificationUnderwayBinding
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessVerificationUnderwaySheet : BaseBottomSheetDialog<SheetBusinessVerificationUnderwayBinding, BaseViewModel>() {

  lateinit var onSubmitClick:(Unit)->Unit

  override fun getLayout(): Int {
    return R.layout.sheet_business_verification_underway
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object{
    fun newInstance(onSubmitClickParam:(Unit)->Unit):BusinessVerificationUnderwaySheet{
      return BusinessVerificationUnderwaySheet().apply {
        onSubmitClick=onSubmitClickParam
      }
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
        onSubmitClick.invoke(Unit)
      }
    }
  }
}
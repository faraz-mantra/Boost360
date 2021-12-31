package com.framework.errorHandling

import android.os.Bundle
import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.databinding.BsheetThankYouResponseBinding
import com.framework.models.BaseViewModel

class ThankYouResponseBottomSheet : BaseBottomSheetDialog<BsheetThankYouResponseBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(): ThankYouResponseBottomSheet {
      val fragment = ThankYouResponseBottomSheet()
      fragment.arguments = Bundle()
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.bsheet_thank_you_response
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    binding?.btnDone?.setOnClickListener {
      finishWithActivity()
    }
  }

  fun finishWithActivity() {
    dismiss()
  }
}
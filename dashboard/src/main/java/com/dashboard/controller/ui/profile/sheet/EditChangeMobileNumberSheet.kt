package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.dashboard.R
import com.dashboard.databinding.SheetChangeMobileNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeMobileNumberSheet : BaseBottomSheetDialog<SheetChangeMobileNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_change_mobile_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnPublish,binding?.rivCloseBottomSheet)

    binding?.cetPhone?.addTextChangedListener {
      binding?.btnPublish?.isEnabled=it?.length==10
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnPublish->{
        showVerifyMobileSheet()
      }
      binding?.rivCloseBottomSheet->dismiss()
    }
  }

  private fun showVerifyMobileSheet() {
    VerifyOtpEmailMobileSheet().show(parentFragmentManager,VerifyOtpEmailMobileSheet::javaClass.name)


  }
}
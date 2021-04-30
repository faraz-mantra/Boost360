package com.boost.presignin.ui

import com.boost.presignin.R
import com.boost.presignin.databinding.ResetLinkBottomSheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ResetLinkBottomSheet : BaseBottomSheetDialog<ResetLinkBottomSheetBinding, BaseViewModel>() {

  var onClick: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.reset_link_bottom_sheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    isCancelable = false
    binding?.btnLogin?.setOnClickListener {
      dismiss()
      onClick()
    }
  }
}
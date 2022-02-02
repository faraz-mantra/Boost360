package com.framework.errorHandling

import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.base.NewBaseBottomSheetDialog
import com.framework.databinding.BsheetProgressBinding
import com.framework.models.BaseViewModel
import com.framework.pref.clientId

class ProgressBottomSheet : NewBaseBottomSheetDialog<BsheetProgressBinding, BaseViewModel>() {

  var onRepublishSuccess: () -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bsheet_progress
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    this.isCancelable = false
    binding?.tvProgressTitle?.text = getString(R.string.please_wait_creating_a_ticket)
  }
}
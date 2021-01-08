package com.dashboard.controller.ui.dialogWelcome

import com.dashboard.R
import com.dashboard.databinding.DialogWelcomeHomeBinding
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel

class WelcomeHomeDialog : BaseDialogFragment<DialogWelcomeHomeBinding, BaseViewModel>()  {

  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFull
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
   return R.layout.dialog_welcome_home
  }

  override fun onCreateView() {
  }
}
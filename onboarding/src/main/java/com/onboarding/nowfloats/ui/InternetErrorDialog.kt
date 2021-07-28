package com.onboarding.nowfloats.ui

import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.NetworkUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogInternetErrorBinding

class InternetErrorDialog : BaseDialogFragment<DialogInternetErrorBinding, BaseViewModel>() {

  var onRetryTapped = {}

  override fun getLayout(): Int {
    return R.layout.dialog_internet_error
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFull
  }

  override fun onCreateView() {
    binding?.blurView?.setBlur(4F)
    isCancelable = false
    binding?.retryBtn?.setOnClickListener {
      if (NetworkUtils.isNetworkConnected()) {
        onRetryTapped()
        dismiss()
      }
    }
  }

}
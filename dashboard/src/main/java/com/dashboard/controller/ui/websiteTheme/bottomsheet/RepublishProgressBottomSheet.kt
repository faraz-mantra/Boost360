package com.dashboard.controller.ui.websiteTheme.bottomsheet

import com.dashboard.R
import com.dashboard.databinding.BsheetWebsiteRepublishingProgressBinding
import com.dashboard.viewmodel.RepublishWebsiteViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.framework.pref.clientId

class RepublishProgressBottomSheet : BaseBottomSheetDialog<BsheetWebsiteRepublishingProgressBinding, RepublishWebsiteViewModel>() {

  var onRepublishSuccess: () -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bsheet_website_republishing_progress
  }

  override fun getViewModelClass(): Class<RepublishWebsiteViewModel> {
    return RepublishWebsiteViewModel::class.java
  }

  override fun onCreateView() {
    this.isCancelable = false
    republishWebsiteApiCall()
  }

  private fun republishWebsiteApiCall() {
    viewModel?.republishWebsite(clientId = clientId, fpTag = sessionManager?.fpTag)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        onRepublishSuccess()
      } else showShortToast(getString(R.string.please_try_again_later))
      dismiss()
    })
  }
}
package com.dashboard.controller.ui.websiteTheme.bottomsheet

import android.os.Bundle
import com.dashboard.R
import com.dashboard.databinding.BsheetWebsiteRepublishingProgressBinding
import com.dashboard.viewmodel.RepublishWebsiteViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import java.util.*

class RepublishProgressBottomSheet : BaseBottomSheetDialog<BsheetWebsiteRepublishingProgressBinding, RepublishWebsiteViewModel>() {

  var onRepublishSuccess: () -> Unit = { }

  companion object {
    @JvmStatic
    fun newInstance(): RepublishProgressBottomSheet {
      val bundle = Bundle().apply {}
      val fragment = RepublishProgressBottomSheet()
      fragment.arguments = bundle
      return fragment
    }
  }

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
    viewModel?.republishWebsite(fpTag = sessionManager?.fpTag)?.observeOnce(this, {
      if (it.isSuccess()) {
        onRepublishSuccess()
      } else showShortToast(getString(R.string.please_try_again_later))
      dismiss()
    })
  }
}
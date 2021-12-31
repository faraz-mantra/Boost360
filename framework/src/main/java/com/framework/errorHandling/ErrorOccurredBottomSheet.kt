package com.framework.errorHandling

import android.view.View
import com.framework.R
import com.framework.base.NewBaseBottomSheetDialog
import com.framework.databinding.BsheetErrorOccurredBinding
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel
import com.framework.pref.BASE_IMAGE_URL
import com.framework.pref.Key_Preferences

class ErrorOccurredBottomSheet(val errorCode: String?, val errorMessage: String?) : NewBaseBottomSheetDialog<BsheetErrorOccurredBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bsheet_error_occurred
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    binding?.tvErrorMessage?.text = errorMessage
    var imageLogoUri = sessionManager?.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
    if (imageLogoUri.isNullOrEmpty().not() && imageLogoUri!!.contains("http").not()) {
      imageLogoUri = BASE_IMAGE_URL + imageLogoUri
    }

    binding?.ivBusinessImage?.let {
      if (imageLogoUri.isNullOrEmpty().not()) {
        baseActivity.glideLoad(mImageView = it, url = imageLogoUri?:"", placeholder = R.drawable.gradient_white, isLoadBitmap = true)
      } else it.setImageResource(R.drawable.placeholder_error)
    }
    setOnClickListener(binding?.btnReportAnError, binding?.btnTryAgain, binding?.ivClose)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnReportAnError -> {
        ReportIssueBottomSheet(errorCode).show(baseActivity.supportFragmentManager, ReportIssueBottomSheet::class.java.name)
        dismiss()
      }
      binding?.btnTryAgain,
      binding?.ivClose -> dismiss()
    }
  }
}
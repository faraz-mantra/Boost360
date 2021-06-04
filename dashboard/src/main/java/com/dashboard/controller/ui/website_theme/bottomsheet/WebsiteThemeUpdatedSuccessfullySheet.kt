package com.dashboard.controller.ui.website_theme.bottomsheet

import android.view.View
import com.appservice.databinding.BottomSheetServiceCreatedSuccessfullyBinding
import com.dashboard.R
import com.dashboard.databinding.BottomSheetThemeUpdatedSuccessfullyBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

enum class TypeSuccess {
  CLOSE, VISIT_WEBSITE
}

class WebsiteThemeUpdatedSuccessfullySheet : BaseBottomSheetDialog<BottomSheetThemeUpdatedSuccessfullyBinding, BaseViewModel>() {

  var onClicked: (value: String) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_theme_updated_successfully
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.civCancel, binding?.visitWebsite)
    isCancelable = false
    binding?.txtMessage?.text = getString( R.string.successfully_update)
    binding?.txtDesc?.text = getString(R.string.your_theme_update_published)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civCancel -> {
        dismiss()
        onClicked(TypeSuccess.CLOSE.name)
      }
      binding?.visitWebsite -> onClicked(TypeSuccess.VISIT_WEBSITE.name)
    }
  }
}
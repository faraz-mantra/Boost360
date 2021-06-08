package com.dashboard.controller.ui.website_theme.bottomsheet

import android.view.View
import com.appservice.databinding.BottomSheetServiceCreatedSuccessfullyBinding
import com.dashboard.R
import com.dashboard.databinding.BottomSheetPublishChangesBinding
import com.dashboard.databinding.BottomSheetResetWebsiteBinding
import com.dashboard.databinding.BottomSheetThemeUpdatedSuccessfullyBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class WebSiteThemePublishBottomSheet : BaseBottomSheetDialog<BottomSheetPublishChangesBinding, BaseViewModel>() {

  var onClicked: (value: String) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_publish_changes
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnDiscard,binding?.btnPublish)
    isCancelable = false
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rivCloseBottomSheet -> {
        dismiss()
        onClicked(TypeSuccess.CLOSE.name)
      }
      binding?.btnDiscard -> onClicked(TypeSuccess.DISCARD.name)
      binding?.btnPublish -> onClicked(TypeSuccess.PUBLISH_CHANGES.name)
    }
  }
}
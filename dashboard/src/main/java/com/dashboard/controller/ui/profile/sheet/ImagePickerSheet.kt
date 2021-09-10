package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetImagePickerBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ImagePickerSheet : BaseBottomSheetDialog<SheetImagePickerBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_image_picker
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}
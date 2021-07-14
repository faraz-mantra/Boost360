package com.appservice.ui.catalog.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomShettImagePickerBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.models.BaseViewModel

enum class ClickType {
  CAMERA, GALLERY, PDF
}

class ImagePickerBottomSheet :
  BaseBottomSheetDialog<BottomShettImagePickerBinding, BaseViewModel>() {

  var onClicked: (type: ClickType) -> Unit = { }
  var isHidePdf = true
  override fun getLayout(): Int {
    return R.layout.bottom_shett_image_picker
  }

  fun isHidePdf(isHidePdf: Boolean?) {
    isHidePdf?.let { this.isHidePdf = it }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    if (isHidePdf) binding?.pdf?.gone()
    setOnClickListener(binding?.camera, binding?.gallery, binding?.close, binding?.pdf)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.camera -> onClicked(ClickType.CAMERA)
      binding?.gallery -> onClicked(ClickType.GALLERY)
      binding?.pdf -> onClicked(ClickType.PDF)
    }
    dismiss()
  }
}
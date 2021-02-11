package com.appservice.ui.catalog.catalogService.listing

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetServiceCreatedSuccessfullyBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

enum class TypeSuccess {
  CLOSE, VISIT_WEBSITE
}

class CreateServiceSuccessBottomSheet : BaseBottomSheetDialog<BottomSheetServiceCreatedSuccessfullyBinding, BaseViewModel>() {

  var onClicked: (value: String) -> Unit = { }
  var isEdit: Boolean = false
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_service_created_successfully
  }

  fun setData(isEdit: Boolean) {
    this.isEdit = isEdit
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.civCancel, binding?.visitWebsite)
    isCancelable = false
    binding?.txtMessage?.text = if (isEdit) getString(R.string.successfully_update) else getString(R.string.successfully_saved)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civCancel -> {
        dismiss()
        onClicked(TypeSuccess.CLOSE.name)
      }
//      binding?.visitWebsite -> onClicked(TypeSuccess.VISIT_WEBSITE.name)
    }
  }
}
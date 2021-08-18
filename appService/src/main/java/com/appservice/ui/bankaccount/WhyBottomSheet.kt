package com.appservice.ui.bankaccount

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetWhyAccountBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class WhyBottomSheet : BaseBottomSheetDialog<BottomSheetWhyAccountBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_why_account
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.understoodBtn)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.understoodBtn -> dismiss()
    }
  }

  override fun getMarginStart(): Int {
    return resources.getDimensionPixelSize(R.dimen.size_10)
  }

  override fun getMarginEnd(): Int {
    return resources.getDimensionPixelSize(R.dimen.size_10)
  }

}
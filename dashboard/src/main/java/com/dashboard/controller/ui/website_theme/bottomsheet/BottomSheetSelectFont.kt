package com.dashboard.controller.ui.website_theme.bottomsheet

import androidx.lifecycle.ViewModel
import com.dashboard.R
import com.dashboard.databinding.BottomSheetSelectFontBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetSelectFont: BaseBottomSheetDialog<BottomSheetSelectFontBinding, BaseViewModel>() {
  override fun getLayout(): Int {
   return R.layout.bottom_sheet_select_font
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}
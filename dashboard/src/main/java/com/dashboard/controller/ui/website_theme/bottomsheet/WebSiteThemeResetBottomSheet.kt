package com.dashboard.controller.ui.website_theme.bottomsheet

import android.graphics.Color
import android.view.View
import com.dashboard.R
import com.dashboard.constant.IntentConstant
import com.dashboard.databinding.BottomSheetResetWebsiteBinding
import com.dashboard.model.websitetheme.ColorsItem
import com.dashboard.model.websitetheme.FontsList
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class WebSiteThemeResetBottomSheet :
  BaseBottomSheetDialog<BottomSheetResetWebsiteBinding, BaseViewModel>() {

  var onClicked: (value: String) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_reset_website
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnBack, binding?.btnRest)
    val fontsList =
      arguments?.getSerializable(IntentConstant.FONT_LIST.name) as? FontsList
    val colorsItem = arguments?.getSerializable(IntentConstant.COLOR_LIST.name) as? List<ColorsItem>
    setData(fontsList, colorsItem)

    isCancelable = false
  }

  private fun setData(fontsList: FontsList?, colorsItem: List<ColorsItem>?) {
    val defaultColor =
      colorsItem?.first { it.defaultColor == true } ?: colorsItem?.first { it.isSelected == true }
    val defaultFont = fontsList?.primary?.first { it?.defaultFont == true }
      ?: fontsList?.primary?.first { it?.isSelected == true }
    binding?.ccvColor?.setCardBackgroundColor(Color.parseColor(defaultColor?.primary?.ifBlank { defaultColor.secondary }))
    binding?.ctfFontName?.text = defaultFont?.description
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rivCloseBottomSheet -> {
        dismiss()
        onClicked(TypeSuccess.CLOSE.name)
      }
      binding?.btnBack -> onClicked(TypeSuccess.GO_BACK.name)
      binding?.btnRest -> onClicked(TypeSuccess.PUBLISH_CHANGES.name)
    }
  }
}
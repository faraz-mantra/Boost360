package com.dashboard.controller.ui.business.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.ui.business.model.BusinessProfileModel
import com.dashboard.databinding.BottomSheetBusinessNameBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel
import com.framework.utils.showKeyBoard

class BusinessNameBottomSheet : BaseBottomSheetDialog<BottomSheetBusinessNameBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_business_name
  }

  private var businessProfileModel: BusinessProfileModel? = null
  var onClicked: (businessName: String) -> Unit = { }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.behavior.isDraggable = false
    setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnPublish)
    this.businessProfileModel = arguments?.get(IntentConstant.BUSINESS_DETAILS.name) as? BusinessProfileModel
    binding?.cetBusinessName?.setText(businessProfileModel?.businessName)
    binding?.cetBusinessName?.setSelection(businessProfileModel?.businessName?.length?:0)
    binding?.btnPublish?.isEnabled = false
    binding?.ctvBusinessNameCount?.text = "${binding?.cetBusinessName?.text?.length}/40"
    binding?.cetBusinessName?.afterTextChanged { updateText(it) }
    baseActivity.showKeyBoard(binding?.cetBusinessName)
  }

  private fun updateText(s: String) {
    binding?.ctvBusinessNameCount?.text = "${s?.length}/40"
    binding?.btnPublish?.isEnabled = binding?.cetBusinessName?.text?.length ?: 0 > 0 && businessProfileModel?.businessName?.trim()!=s.trim()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        val businessName = binding?.cetBusinessName?.text.toString()
        businessProfileModel?.businessName = businessName
        onClicked(binding?.cetBusinessName?.text.toString())
        dismiss()
      }
      binding?.rivCloseBottomSheet -> {
        dismiss()
      }
    }
  }
}
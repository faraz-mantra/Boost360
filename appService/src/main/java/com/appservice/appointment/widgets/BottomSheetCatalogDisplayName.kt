package com.appservice.appointment.widgets

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.appointment.model.UserFpDetailsResponse
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetCatalogDisplayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class BottomSheetCatalogDisplayName : BaseBottomSheetDialog<BottomSheetCatalogDisplayBinding, BaseViewModel>() {
  private var fpDetails: UserFpDetailsResponse? = null
  var customText: String? = null
  var onDoneClicked: () -> Unit = {}
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_catalog_display
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.setCancelable(false)
    this.fpDetails = arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse
    if (fpDetails != null) {
      binding?.radioGroup?.check(R.id.radio_custom_text)
      radioCustomText()
    } else {
      binding?.radioGroup?.check(R.id.radio_service)
      initRadio()

    }
    setOnClickListener(binding?.btnCancel, binding?.btnProceed)
    binding?.radioGroup?.setOnCheckedChangeListener { _, checkedId ->
      when (checkedId) {
        R.id.radio_service -> {
          initRadio()
        }
        R.id.radio_custom_text -> {
          radioCustomText()

        }
        else -> {
          //do nothing
        }
      }
    }
  }

  private fun radioCustomText() {
    binding?.ctvCustomDisplayHint?.visible()
    binding?.btnCancel?.visible()
    binding?.btnProceed?.text = getString(R.string.proceed)
    binding?.ctvCustomDisplayHint?.setText(fpDetails?.productCategoryVerb)
  }

  private fun initRadio() {
    binding?.ctvCustomDisplayHint?.gone()
    binding?.btnCancel?.gone()
    binding?.btnProceed?.text = getString(R.string.close)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> {
        dismiss()
      }
      binding?.btnProceed -> {
        customText = if (binding?.radioGroup?.checkedRadioButtonId != R.id.radio_service) {
          binding?.ctvCustomDisplayHint?.text.toString()
        } else {
          "SERVICES"
        }
        showConfirmingChange()
        dismiss()
      }
    }
  }

  private fun showConfirmingChange() {
    val bottomSheetCatalogDisplayName = BottomSheetConfirmingChange()
    val bundle = Bundle()
    bundle.putString(IntentConstant.CATALOG_CUSTOM_NAME.name, customText)
    bundle.putSerializable(IntentConstant.CATALOG_DATA.name, fpDetails)
    bottomSheetCatalogDisplayName.arguments = bundle
    bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetConfirmingChange::class.java.name)
  }
}
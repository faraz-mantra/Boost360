package com.appservice.ui.aptsetting.widgets

import android.view.View
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetCatalogDisplayBinding
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.ui.aptsetting.ui.getProductType
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.onboarding.nowfloats.extensions.afterTextChanged

class BottomSheetCatalogDisplayName : BaseBottomSheetDialog<BottomSheetCatalogDisplayBinding, BaseViewModel>() {

  private var fpDetails: UserFpDetailsResponse? = null
  var customText: String? = null
  var onClicked: (customText: String?, fpDetails: UserFpDetailsResponse?) -> Unit = { _: String?, _: UserFpDetailsResponse? -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_catalog_display
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    sessionManager = UserSessionManager(baseActivity)
    binding?.radioService?.text = getProductType(sessionManager?.fP_AppExperienceCode)
    dialog.setCancelable(false)
    this.fpDetails = arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse
    binding?.ctvCustomDisplayHint?.afterTextChanged { binding?.textCount?.text = "${12 - it.length}" }
    if (this.fpDetails?.productCategoryVerb.isNullOrEmpty().not()) {
      binding?.radioGroup?.check(R.id.radio_custom_text)
      radioCustomText()
    } else {
      binding?.radioGroup?.check(R.id.radio_service)
      initRadio()
    }
    setOnClickListener(binding?.btnCancel, binding?.btnProceed)
    binding?.radioGroup?.setOnCheckedChangeListener { _, checkedId ->
      when (checkedId) {
        R.id.radio_service -> initRadio()
        R.id.radio_custom_text -> radioCustomText()
        else -> {
        }
      }
    }
  }

  private fun radioCustomText() {
    binding?.customDisplayView?.visible()
    binding?.btnCancel?.visible()
    binding?.btnProceed?.text = getString(R.string.proceed)
    binding?.ctvCustomDisplayHint?.setText(fpDetails?.productCategoryVerb)
  }

  private fun initRadio() {
    binding?.customDisplayView?.gone()
    if (this.fpDetails?.productCategoryVerb.isNullOrEmpty().not()) {
      binding?.btnCancel?.visible()
      binding?.btnProceed?.text = getString(R.string.proceed)
    } else {
      binding?.btnCancel?.gone()
      binding?.btnProceed?.text = getString(R.string.close)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> dismiss()
      binding?.btnProceed -> {
        customText = if (binding?.radioGroup?.checkedRadioButtonId != R.id.radio_service) {
          binding?.ctvCustomDisplayHint?.text.toString()
        } else getProductType(sessionManager?.fP_AppExperienceCode)
        if (binding?.btnProceed?.text?.equals(getString(R.string.proceed)) == true) onClicked(customText, fpDetails)
        dismiss()
      }
    }
  }
}
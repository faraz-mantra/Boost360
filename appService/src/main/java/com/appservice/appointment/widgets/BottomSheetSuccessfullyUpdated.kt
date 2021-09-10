package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.appointment.model.UserFpDetailsResponse
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSuccessfullyPublishedBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class BottomSheetSuccessfullyUpdated : BaseBottomSheetDialog<BottomSheetSuccessfullyPublishedBinding, BaseViewModel>() {
  private var catalogName: String? = null
  private var fpDetails: UserFpDetailsResponse? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_successfully_published
  }
  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
  override fun onCreateView() {
    dialog.setCancelable(false)
    sessionManager = UserSessionManager(baseActivity)
    setOnClickListener(binding?.civCancel)
    this.fpDetails = arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse
    this.catalogName = arguments?.getString(IntentConstant.CATALOG_CUSTOM_NAME.name)
    binding?.ctvLink?.text = "${sessionManager?.rootAliasURI}/${catalogName}"
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civCancel -> {
        baseActivity.onBackPressed()
        dismiss()
      }
    }
  }
}
package com.appservice.appointment.widgets

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.appointment.model.UpdateInfoRequest
import com.appservice.appointment.model.UserFpDetailsResponse
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetConfirmingChangesBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager

class BottomSheetConfirmingChange : BaseBottomSheetDialog<BottomSheetConfirmingChangesBinding, AppointmentSettingsViewModel>() {
  private var catalogName: String? = null
  private var fpDetails: UserFpDetailsResponse? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_confirming_changes
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun onCreateView() {
    dialog.setCancelable(false)
    sessionManager = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnGoBack, binding?.btnYesChange)
    this.fpDetails = arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse
    this.catalogName = arguments?.getString(IntentConstant.CATALOG_CUSTOM_NAME.name)
    binding?.ctvNewDisplayText?.text = catalogName
    binding?.ctvNewPageUrl?.text = "${sessionManager?.rootAliasURI}/${catalogName}"
    binding?.ccbAgree?.setOnCheckedChangeListener { _, isChecked ->
      when (isChecked) {
        true -> binding?.btnYesChange?.visible()
        false -> binding?.btnYesChange?.gone()
      }
    }

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnGoBack -> {
        dismiss()
      }
      binding?.btnYesChange -> {
        updateServiceCategoryVerb()
      }
    }
  }

  private fun updateServiceCategoryVerb() {
    viewModel?.updateCategoryInfo(getRequest())?.observeOnce(viewLifecycleOwner,{
      when (it.isSuccess()) {
        true -> {
          showSuccessfullyUpdated()
          dismiss()
        }
        else -> {

        }
      }
    })
  }

  private fun getRequest(): UpdateInfoRequest {
    val updateRequest = UpdateInfoRequest()
    return updateRequest.apply {
      tag = sessionManager?.fpTag
      productCategoryVerb = catalogName
    }
  }

  private fun showSuccessfullyUpdated() {
    val bottomSheetCatalogDisplayName = BottomSheetSuccessfullyUpdated()
    val bundle = Bundle()
    bundle.putString(IntentConstant.CATALOG_CUSTOM_NAME.name, catalogName)
    bundle.putSerializable(IntentConstant.CATALOG_DATA.name, fpDetails)
    bottomSheetCatalogDisplayName.arguments = bundle
    bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetSuccessfullyUpdated::class.java.name)
  }
}
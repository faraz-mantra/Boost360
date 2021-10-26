package com.appservice.ui.aptsetting.widgets

import android.view.View
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetConfirmingChangesBinding
import com.appservice.model.aptsetting.ProductCategoryVerbRequest
import com.appservice.model.aptsetting.UpdatesItem
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml

class BottomSheetConfirmingChange : BaseBottomSheetDialog<BottomSheetConfirmingChangesBinding, AppointmentSettingsViewModel>() {

  private var catalogName: String? = null
  private var fpDetails: UserFpDetailsResponse? = null
  var onClicked: (catalogName: String?, fpDetails: UserFpDetailsResponse?) -> Unit = { _: String?, _: UserFpDetailsResponse? -> }

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
    binding?.ctvNewDisplayText?.text = fromHtml("<b>New display text:</b> $catalogName")
    binding?.ctvNewPageUrl?.text = fromHtml("<b>New page URL:</b><br /><u>${sessionManager?.getDomainName()}/$catalogName</u>")
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
    binding?.btnYesChange?.isEnabled = false
    viewModel?.updateProductCategoryVerb(getRequest())?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        onClicked(catalogName,fpDetails)
        dismiss()
      } else showShortToast("Error updating catalog!")
      binding?.btnYesChange?.isEnabled = true
    })
  }

  private fun getRequest(): ProductCategoryVerbRequest {
    val updateRequest = ProductCategoryVerbRequest()
    return updateRequest.apply {
      fpTag = sessionManager?.fpTag
      updates = arrayListOf(UpdatesItem(catalogName, "PRODUCTCATEGORYVERB"))
      clientID = clientId
    }
  }
}
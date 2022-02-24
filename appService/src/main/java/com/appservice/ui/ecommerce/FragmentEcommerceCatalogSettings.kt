package com.appservice.ui.ecommerce

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.ui.aptsetting.widgets.BottomSheetCatalogDisplayName
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentEcomCatalogSettingBinding
import com.appservice.model.aptsetting.AppointmentStatusResponse
import com.appservice.model.aptsetting.CatalogSetup
import com.appservice.ui.aptsetting.widgets.BottomSheetConfirmingChange
import com.appservice.ui.aptsetting.widgets.BottomSheetSuccessfullyUpdated
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.webengageconstant.*
import java.util.*

class FragmentEcommerceCatalogSettings : AppBaseFragment<FragmentEcomCatalogSettingBinding, AppointmentSettingsViewModel>() {

  private var response: UserFpDetailsResponse? = null

  companion object {
    fun newInstance(): FragmentEcommerceCatalogSettings {
      return FragmentEcommerceCatalogSettings()
    }
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.fragment_ecom_catalog_setting
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(ECOMMERCE_CATLOG_SETUP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.ctvChangeServices, binding?.ctvProductVerbUrl)
    val data = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? AppointmentStatusResponse.TilesModel
    val catalogSetup = data?.tile as? CatalogSetup
    setData(catalogSetup)
    getFpDetails()
  }

  private fun setData(catalogSetup: CatalogSetup?) {
    binding?.edtTextSlab?.hint = "${(catalogSetup?.defaultGSTSlab ?: 0).toString()}%"
  }

  private fun getFpDetails() {
    showProgress()
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(sessionLocal.fPID ?: "", map)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      this.response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        binding?.ctvProductVerb?.text = response?.productCategory(baseActivity)?.capitalizeUtil()
        binding?.ctvProductVerbUrl?.text = fromHtml("<pre>URL: <span style=\"color: #4a4a4a;\"><u>${sessionLocal.getDomainName()}<b>/${response?.productCategoryVerb(baseActivity)}</b></u></span></pre>")
        sessionLocal.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB, response?.productCategoryVerb)
        onCatalogSetupAddedOrUpdated(response?.productCategoryVerb.isNullOrEmpty().not())
      }
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ctvChangeServices -> {
        WebEngageController.trackEvent(CATLOG_SETUP_CHANGE_PRODUCT, CLICK, NO_EVENT_VALUE)
        showCatalogDisplayName()
      }
    }
  }

  private fun showCatalogDisplayName() {
    BottomSheetCatalogDisplayName().apply {
      arguments = Bundle().apply { putSerializable(IntentConstant.CATALOG_DATA.name, response) }
      onClicked = { customText, fpDetails ->
        BottomSheetConfirmingChange().apply {
          arguments = Bundle().apply {
            putString(IntentConstant.CATALOG_CUSTOM_NAME.name, customText)
            putSerializable(IntentConstant.CATALOG_DATA.name, fpDetails)
          }
          onClicked = { catalogName, fpDetails ->
            BottomSheetSuccessfullyUpdated().apply {
              arguments = Bundle().apply {
                putString(IntentConstant.CATALOG_CUSTOM_NAME.name, catalogName)
                putSerializable(IntentConstant.CATALOG_DATA.name, fpDetails)
              }
              onSuccessClicked = {
                getFpDetails()
              }
              show(this@FragmentEcommerceCatalogSettings.parentFragmentManager, BottomSheetSuccessfullyUpdated::class.java.name)
            }
          }
          show(this@FragmentEcommerceCatalogSettings.parentFragmentManager, BottomSheetConfirmingChange::class.java.name)
        }
      }
      show(this@FragmentEcommerceCatalogSettings.parentFragmentManager, BottomSheetCatalogDisplayName::class.java.name)
    }
  }
}
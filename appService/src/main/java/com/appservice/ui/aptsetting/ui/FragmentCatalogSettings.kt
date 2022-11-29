package com.appservice.ui.aptsetting.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.ui.aptsetting.widgets.BottomSheetCatalogDisplayName
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentCatalogSettingBinding
import com.appservice.model.aptsetting.AppointmentStatusResponse
import com.appservice.model.aptsetting.CatalogSetup
import com.appservice.model.aptsetting.GstSlabRequest
import com.appservice.ui.aptsetting.widgets.BottomSheetConfirmingChange
import com.appservice.ui.aptsetting.widgets.BottomSheetGstSlab
import com.appservice.ui.aptsetting.widgets.BottomSheetSuccessfullyUpdated
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.webengageconstant.*
import com.framework.webengageconstant.APPOINTMENT_CATLOG_SETUP_PAGE_LOAD
import java.util.*

class FragmentCatalogSettings : AppBaseFragment<FragmentCatalogSettingBinding, AppointmentSettingsViewModel>() {

  private var response: UserFpDetailsResponse? = null
  private var catalogSetup: CatalogSetup? = null
  private var gstSlab: Int = 0

  override fun getLayout(): Int {
    return R.layout.fragment_catalog_setting
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentCatalogSettings {
      return FragmentCatalogSettings()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(baseActivity)
    WebEngageController.trackEvent(APPOINTMENT_CATLOG_SETUP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.ctvChangeServices, binding?.ctvWebsiteUrl, binding?.edtTextSlab)
    val data = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? AppointmentStatusResponse.TilesModel
    catalogSetup = data?.tile as? CatalogSetup
    if (catalogSetup != null) setData(catalogSetup) else catalogApiGetGstData()
    binding?.viewGst?.isVisible = isDoctorClinic.not()
    getFpDetails()
  }

  private fun catalogApiGetGstData() {
    viewModel?.getAppointmentCatalogStatus(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner) {
      val dataItem = it as? AppointmentStatusResponse
      if (dataItem?.isSuccess() == true && dataItem.result != null) {
        catalogSetup = dataItem.result?.catalogSetup
        setData(catalogSetup)
      }
    }
  }

  private fun setData(catalogSetup: CatalogSetup?) {
    gstSlab = catalogSetup?.getGstSlabInt() ?: 0
    binding?.edtTextSlab?.setText("${(catalogSetup?.getGstSlabInt() ?: 0)}%")
  }

  private fun getFpDetails() {
    showProgress()
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(sessionLocal.fPID ?: "", map)?.observeOnce(viewLifecycleOwner) {
      hideProgress()
      this.response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        binding?.ctvService?.text = response?.productCategory(baseActivity)?.capitalizeUtil()
        binding?.ctvWebsiteUrl?.text = fromHtml("<pre>URL: <span style=\"color: #4a4a4a;\"><u>${sessionLocal.getDomainName()}<b>/${response?.productCategoryVerb(baseActivity)}</b></u></span></pre>")
        sessionLocal.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB, response?.productCategoryVerb)
        onCatalogSetupAddedOrUpdated(response?.productCategoryVerb.isNullOrEmpty().not())
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ctvChangeServices -> {
        WebEngageController.trackEvent(CATLOG_SETUP_CHANGE_SERVICE, CLICK, NO_EVENT_VALUE)
        showCatalogDisplayName()
      }
      binding?.edtTextSlab -> {
        WebEngageController.trackEvent(CATLOG_SETUP_CHANGE_GST, CLICK, NO_EVENT_VALUE)
        BottomSheetGstSlab().apply {
          arguments = Bundle().apply { putInt(IntentConstant.GST_DATA.name, gstSlab) }
          onClicked = { gstSlab ->
            this@FragmentCatalogSettings.gstSlab = gstSlab
            updateGstData()
          }
          show(this@FragmentCatalogSettings.parentFragmentManager, BottomSheetGstSlab::class.java.name)
        }
      }
    }
  }

  private fun updateGstData() {
    showProgress()
    viewModel?.updateGstSlab(GstSlabRequest(clientId, sessionLocal.fPID, gstSlab))?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) binding?.edtTextSlab?.setText("$gstSlab%")
      else showShortToast(it.messageN())
      hideProgress()
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
              show(this@FragmentCatalogSettings.parentFragmentManager, BottomSheetSuccessfullyUpdated::class.java.name)
            }
          }
          show(this@FragmentCatalogSettings.parentFragmentManager, BottomSheetConfirmingChange::class.java.name)
        }
      }
      show(this@FragmentCatalogSettings.parentFragmentManager, BottomSheetCatalogDisplayName::class.java.name)
    }
  }

}
package com.appservice.ui.aptsetting.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentCatalogSettingBinding
import com.appservice.model.aptsetting.AppointmentStatusResponse
import com.appservice.model.aptsetting.CatalogSetup
import com.appservice.model.aptsetting.GstSlabRequest
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.model.businessmodel.BusinessProfileUpdateRequest
import com.appservice.model.businessmodel.Update
import com.appservice.ui.aptsetting.widgets.BottomSheetCatalogDisplayName
import com.appservice.ui.aptsetting.widgets.BottomSheetConfirmingChange
import com.appservice.ui.aptsetting.widgets.BottomSheetGstSlab
import com.appservice.ui.aptsetting.widgets.BottomSheetSuccessfullyUpdated
import com.appservice.ui.ecommerce.bottomsheets.BottomSheetCategoryRename
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.*
import com.framework.utils.fromHtml
import com.framework.webengageconstant.*
import java.util.*

class FragmentCatalogSettings : AppBaseFragment<FragmentCatalogSettingBinding, AppointmentSettingsViewModel>() {

  private var response: UserFpDetailsResponse? = null
  private var catalogSetup: CatalogSetup? = null
  private var gstSlab: Int = 0
  var catalogueName: TextView? = null

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
    catalogueName = view?.findViewById(R.id.catalouge_name_label)
    setOnClickListener(binding?.ctvChangeServices, binding?.ctvWebsiteUrl, binding?.edtTextSlab, binding?.renameView, binding?.btnSaveDetails)
    val data = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? AppointmentStatusResponse.TilesModel
    catalogSetup = data?.tile as? CatalogSetup
    if (catalogSetup != null) setData(catalogSetup) else catalogApiGetGstData()
    binding?.viewGst?.isVisible = isDoctorClinic.not()
    getFpDetails()
    binding.noServiceSwitch.setOnToggledListener { _, isChecked ->
      sessionLocal.noServiceSlot = isChecked
      if (isChecked){
        binding.serviceTimesView.visibility = View.GONE
        sessionLocal.serviceTiming = !isChecked
      }else{
        binding.serviceTimesView.visibility = View.VISIBLE
      }
    }
    binding.serviceTimingsSwitch.setOnToggledListener { _, isChecked ->
      sessionLocal.serviceTiming = isChecked
    }
    binding.bulkBookingSwitch.setOnToggledListener { _, isChecked ->
      sessionLocal.bulkBooking = isChecked
    }
    binding.catalougeSwitch.setOnToggledListener { _, isChecked ->
      sessionLocal.isCustomCta = isChecked
      if (isChecked){
        binding.catalougeRenameView.visibility = View.VISIBLE
      }else{
        binding.catalougeRenameView.visibility = View.GONE
      }
    }
  }

  private fun catalogApiGetGstData() {
    viewModel?.getAppointmentCatalogStatus(sessionLocal.fPID!!, clientId)?.observeOnce(viewLifecycleOwner) {
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
        sessionLocal.noServiceSlot = response!!.noServiceSlot
        sessionLocal.serviceTiming = response!!.sameServiceSlot
        sessionLocal.bulkBooking = response!!.isBulkBooking
        sessionLocal.isCustomCta = response!!.isCustomCta
        sessionLocal.customCta = response!!.customCta

        binding?.ctvService?.text = response?.productCategory(baseActivity)?.capitalizeUtil()
        binding?.ctvWebsiteUrl?.text = fromHtml("<pre>URL: <span style=\"color: #4a4a4a;\"><u>${sessionLocal.getDomainName()}<b>/${response?.productCategoryVerb(baseActivity)}</b></u></span></pre>")
        sessionLocal.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB, response?.productCategoryVerb)
        onCatalogSetupAddedOrUpdated(response?.productCategoryVerb.isNullOrEmpty().not())
      }
      if (sessionLocal.fP_AppExperienceCode!! == "SVC" || sessionLocal.fP_AppExperienceCode!! == "SAL" || sessionLocal.fP_AppExperienceCode!! == "CAF"){
        binding.catalogueView.visibility = View.VISIBLE
        binding.catalougeSwitch.isOn = sessionLocal.isCustomCta!!

        if (sessionLocal.isCustomCta!!){
          binding.catalougeRenameView.visibility = View.VISIBLE
          binding.catalougeNameLabel.text = sessionLocal.customCta
        } else {
          binding.catalougeRenameView.visibility = View.GONE
        }
        binding.btnSaveDetails.visibility = View.VISIBLE
      }
      if (sessionLocal.fP_AppExperienceCode!! == "SVC" || sessionLocal.fP_AppExperienceCode!! == "SAL" || sessionLocal.fP_AppExperienceCode!! == "SPA"){
        binding.bulkBookingView.visibility = View.VISIBLE
        binding.bulkBookingSwitch.isOn = sessionLocal.bulkBooking!!

        binding.serviceSlotsView.visibility = View.VISIBLE
        binding.noServiceSwitch.isOn = sessionLocal.noServiceSlot!!

        if (!sessionLocal.noServiceSlot!!){
          binding.serviceTimesView.visibility = View.VISIBLE
          binding.serviceTimingsSwitch.isOn = sessionLocal.serviceTiming!!
        } else {
          binding.serviceTimesView.visibility = View.GONE
        }
        binding.btnSaveDetails.visibility = View.VISIBLE
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
      binding.btnSaveDetails -> {
        val businessProfileUpdateUrl = "https://api2.withfloats.com/Discover/v1/FloatingPoint/update"
        val businessProfileUpdateRequest = BusinessProfileUpdateRequest()
        businessProfileUpdateRequest.clientId = clientId
        businessProfileUpdateRequest.fpTag = sessionLocal.fpTag
        val updateItemList = arrayListOf<Update>()
        val bulkBookingInfo = Update()
        bulkBookingInfo.key="ISBULKBOOKING";
        bulkBookingInfo.value=sessionLocal.bulkBooking.toString()
        updateItemList.add(bulkBookingInfo)
        val storeToggleInfo = Update()
        storeToggleInfo.key="STORETOGGLE";
        val value ="${sessionLocal.noServiceSlot.toString()}#${sessionLocal.serviceTiming.toString()}"
        storeToggleInfo.value=value
        updateItemList.add(storeToggleInfo)
        val ctaInfo = Update()
        ctaInfo.key = "CTATOGGLE";
        ctaInfo.value = "${sessionLocal.isCustomCta.toString()}#${sessionLocal.customCta}"
        updateItemList.add(ctaInfo)
        businessProfileUpdateRequest.updates = updateItemList
        showProgress()
        viewModel?.updateBusinessDetails(businessProfileUpdateUrl,businessProfileUpdateRequest)
          ?.observeOnce(viewLifecycleOwner) {
            if (it.isSuccess()) {
              showShortToast("Successfully updated the catalogue information")
            } else {
              showShortToast("Error while updating the catalogue information")
            }
            hideProgress()
            activity!!.onBackPressed()
          }
      }
      binding.renameView -> {
        BottomSheetCategoryRename(this,null,1).apply {
          show(this@FragmentCatalogSettings.parentFragmentManager, BottomSheetCategoryRename::class.java.name)
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
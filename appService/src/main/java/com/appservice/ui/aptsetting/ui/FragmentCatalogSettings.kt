package com.appservice.ui.aptsetting.ui

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.ui.aptsetting.widgets.BottomSheetCatalogDisplayName
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentCatalogSettingBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import java.util.*

class FragmentCatalogSettings : AppBaseFragment<FragmentCatalogSettingBinding, AppointmentSettingsViewModel>() {
  private var response: UserFpDetailsResponse? = null

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
    setOnClickListener(binding?.ctvChangeServices, binding?.ctvWebsiteUrl)
    getFpDetails()
  }

  private fun getFpDetails() {
    showProgress()
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(sessionLocal?.fPID ?: "", map)
      ?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        this.response = it as? UserFpDetailsResponse
        if (it.isSuccess() && response != null) {
          binding?.ctvService?.text = response?.productCategoryVerb
          binding?.ctvWebsiteUrl?.text = "${sessionLocal.rootAliasURI}/${response?.productCategoryVerb}"
          sessionLocal.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB,response?.productCategoryVerb)
        } else {
        }
      })
  }
  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ctvChangeServices -> {
        showCatalogDisplayName()
      }
    }
  }
  private fun showCatalogDisplayName() {
    val bottomSheetCatalogDisplayName = BottomSheetCatalogDisplayName()
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.CATALOG_DATA.name, response)
    bottomSheetCatalogDisplayName.arguments = bundle
    bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetCatalogDisplayName::class.java.name)
  }

}
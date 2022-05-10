package com.appservice.ui.aptsetting.ui

import android.os.Bundle
import android.widget.SearchView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.base.isDoctorProfile
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAppointmentSettingsBinding
import com.appservice.model.aptsetting.AppointmentStatusResponse
import com.appservice.model.aptsetting.IconType
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.APPOINTMENT_SETTING_PAGE_LOAD
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import java.util.*

class FragmentAppointmentSettings : AppBaseFragment<FragmentAppointmentSettingsBinding, AppointmentSettingsViewModel>(), RecyclerItemClickListener {

  private var adapter: AppBaseRecyclerViewAdapter<AppointmentStatusResponse.TilesModel>? = null
  private var copyList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()
  private var finalList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()
  private var filteredList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()
  private var isFirst: Boolean = true

  override fun getLayout(): Int {
    return R.layout.fragment_appointment_settings
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentAppointmentSettings {
      return FragmentAppointmentSettings()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(APPOINTMENT_SETTING_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    clearSearchFocus()
    binding?.svSettings?.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return false
      }

    })
  }

  override fun onResume() {
    super.onResume()
    getStatusData()
  }

  private fun getStatusData() {
    if (isFirst) showProgress()
    viewModel?.getAppointmentCatalogStatus(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner) {
      val dataItem = it as? AppointmentStatusResponse
      if (dataItem?.isSuccess() == true && dataItem.result != null) {
        setUpRecyclerView(dataItem.result!!.getAppointmentTilesArray(isDoctorProfile(sessionLocal.fP_AppExperienceCode)))
      } else showShortToast("Appointment setting data getting error!")
      isFirst = false
      hideProgress()
    }
  }

  private fun filterList(newText: String?) {
    filteredList?.clear()
    if (newText?.isNotEmpty() == true) {
      copyList?.forEach {
        if (it.title?.toLowerCase(Locale.ROOT)?.contains(newText.toLowerCase(Locale.ROOT)) == true) {
          filteredList?.add(it)
        }
      }
      adapter?.updateList(filteredList ?: arrayListOf())
    } else {
      adapter?.updateList(finalList ?: arrayListOf())

    }
  }

  private fun setUpRecyclerView(tilesArray: ArrayList<AppointmentStatusResponse.TilesModel>) {
    this.copyList?.addAll(tilesArray)
    this.finalList?.addAll(tilesArray)
    this.adapter = AppBaseRecyclerViewAdapter(baseActivity, tilesArray, this@FragmentAppointmentSettings)
    binding?.rvTiles?.setHasFixedSize(true)
    binding?.rvTiles?.adapter = adapter
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val data = item as? AppointmentStatusResponse.TilesModel
    data?.let { clickActionButton(it) }
  }

  private fun clearSearchFocus() {
    binding?.svSettings?.clearFocus()
  }


  private fun clickActionButton(data: AppointmentStatusResponse.TilesModel) {
    when (IconType.fromName(data.icon)) {
      IconType.catalog_setup -> startFragmentActivity(FragmentType.APPOINTMENT_CATALOG_SETTINGS, bundle = Bundle().apply { putSerializable(IntentConstant.OBJECT_DATA.name, data) })
      IconType.consultation_settings -> startFragmentActivity(FragmentType.CONSULTATION_APT_SETTINGS)
      IconType.customer_invoice_setup -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE)
      IconType.payment_collection -> startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS)
      IconType.policies -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES)
      IconType.business_verification->startFragmentActivity(FragmentType.ECOMMERCE_BUSINESS_VERIFICATION)
      else -> {
      }
    }
  }
}
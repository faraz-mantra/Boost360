package com.appservice.ui.aptsetting.ui

import android.widget.SearchView
import com.appservice.R
import com.appservice.model.aptsetting.AppointmentStatusResponse
import com.appservice.model.aptsetting.IconType
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentAppointmentSettingsBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import java.util.*

class FragmentAppointmentSettings : AppBaseFragment<FragmentAppointmentSettingsBinding, AppointmentSettingsViewModel>(), RecyclerItemClickListener {

  private var adapter: AppBaseRecyclerViewAdapter<AppointmentStatusResponse.TilesModel>? = null
  private var copyList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()
  private var finalList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()
  var filteredList: ArrayList<AppointmentStatusResponse.TilesModel>? = arrayListOf()

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
    getStatusData()
  }

  private fun getStatusData() {
    showProgress()
    viewModel?.getAppointmentCatalogStatus(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      when (it.isSuccess()) {
        true -> {
          val dataItem = it as? AppointmentStatusResponse
          if (dataItem != null) {
            setUpRecyclerView(dataItem.result?.getAppointmentTilesArray()!!)
          }
        }
        else -> {
        }
      }
    })
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
    data?.icon?.let { IconType.fromName(it) }?.let { clickActionButton(it) }
  }

  private fun clearSearchFocus() {
    binding?.svSettings?.clearFocus()
  }


  private fun clickActionButton(type: IconType) {
    when (type) {
      IconType.catalog_setup -> startFragmentActivity(FragmentType.APPOINTMENT_CATALOG_SETTINGS)
      IconType.customer_invoice_setup -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE)
      IconType.payment_collection -> startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS)
      IconType.policies -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES)
      else -> {
      }
    }
  }
}


fun getProductType(category_code: String?): String {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "Services"
    else -> "Products"
  }
}
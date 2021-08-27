package com.appservice.appointment.ui

import android.widget.SearchView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentAppointmentSettingsBinding
import com.appservice.model.catalog.CatalogTileModel
import com.appservice.model.catalog.IconType
import com.appservice.model.catalog.TilesItem
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import java.util.*

class FragmentAppointmentSettings : AppBaseFragment<FragmentAppointmentSettingsBinding, AppointmentSettingsViewModel>(), RecyclerItemClickListener {
  private var copyList: ArrayList<TilesItem>? = null

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
    setUpRecyclerView()
//        setOnClickListener(binding?.catalogSetup, binding?.paymentCollectionSetup, binding?.customerInvoiceSetup, binding?.policiesForCustomer)
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

  private fun filterList(newText: String?) {

  }

  private fun setUpRecyclerView() {
    viewModel?.getAppointmentSettingsTiles(baseActivity.baseContext)?.observeOnce(viewLifecycleOwner, {
      val data = it as? CatalogTileModel
      when (data != null) {
        true -> {
          this.copyList = data.tiles
          val adapter = AppBaseRecyclerViewAdapter(baseActivity, data.tiles as ArrayList<TilesItem>, this@FragmentAppointmentSettings)
          binding?.rvTiles?.setHasFixedSize(true)
          binding?.rvTiles?.adapter = adapter
        }
      }
    })

  }

  private fun clearSearchFocus() {
    // closes the soft keyboard when this fragment loafds
    binding?.svSettings?.clearFocus()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val data = item as? TilesItem
    data?.icon?.let { IconType.fromName(it) }?.let { clickActionButton(it) }


  }

  private fun clickActionButton(type: IconType) {
    when (type) {
      IconType.catalog_setup -> startFragmentActivity(FragmentType.APPOINTMENT_CATALOG_SETTINGS)
      IconType.customer_invoice_setup -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE)
      IconType.payment_collection -> startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS)
      IconType.policies -> startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES)
    }
  }

}
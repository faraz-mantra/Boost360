package com.appservice.ecommercesettings.ui

import android.widget.SearchView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentEcommerceSettingsBinding
import com.appservice.model.catalog.*
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce
import java.util.*

class FragmentEcommerceSettings : AppBaseFragment<FragmentEcommerceSettingsBinding, AppointmentSettingsViewModel>(), RecyclerItemClickListener {
  private var adapter: AppBaseRecyclerViewAdapter<EcommerceTilesItem>? = null
  private var copyList: ArrayList<EcommerceTilesItem>? = arrayListOf()
  private var finalList: ArrayList<EcommerceTilesItem>? = arrayListOf()
  var filteredList: ArrayList<EcommerceTilesItem>? = arrayListOf()
  override fun getLayout(): Int {
    return R.layout.fragment_ecommerce_settings
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentEcommerceSettings {
      return FragmentEcommerceSettings()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
//    setUpRecyclerView()
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

  private fun clearSearchFocus() {
    // closes the soft keyboard when this fragment loads
    binding?.svSettings?.clearFocus()
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

//  private fun setUpRecyclerView() {
//    viewModel?.getAppointmentSettingsTiles(baseActivity.baseContext)?.observeOnce(viewLifecycleOwner, {
//      val data = it as? EcommerceCatalogTileModel
//      when (data != null) {
//        true -> {
//          this.copyList?.addAll(data.tiles!!)
//          this.finalList?.addAll(data.tiles!!)
//          this.adapter = AppBaseRecyclerViewAdapter(baseActivity, data.tiles as ArrayList<EcommerceTilesItem>, this@FragmentEcommerceSettings)
//          binding?.rvTiles?.setHasFixedSize(true)
//          binding?.rvTiles?.adapter = adapter
//        }
//      }
//    })
//
//  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val data = item as? EcommerceTilesItem
    data?.icon?.let { EcommerceIconType.fromName(it) }?.let { clickActionButton(it) }


  }

  private fun clickActionButton(type: EcommerceIconType) {
    when (type) {
      EcommerceIconType.catalog_setup -> startFragmentActivity(FragmentType.ECOMMERCE_CATALOG_SETTINGS)
      EcommerceIconType.delivery_setup -> startFragmentActivity(FragmentType.ECOMMERCE_DELIVERY_CONFIG)
      EcommerceIconType.customer_invoice_setup -> startFragmentActivity(FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE)
      EcommerceIconType.payment_collection -> startFragmentActivity(FragmentType.ECOMMERCE_PAYMENT_SETTINGS)
      EcommerceIconType.policies -> startFragmentActivity(FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES)
    }
  }
}
package com.dashboard.controller.ui.allAddOns

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.FragmentAllBoostAddOnsBinding
import com.dashboard.model.live.addOns.AllBoostAddOnsData
import com.dashboard.model.live.addOns.AllBoostAddOnsDataResponse
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.viewmodel.AddOnsViewModel
import com.framework.extensions.observeOnce
import java.util.*

class AllBoostAddonsFragment : AppBaseFragment<FragmentAllBoostAddOnsBinding, AddOnsViewModel>(), RecyclerItemClickListener {

  private var searchView: SearchView? = null
  var adapterAddOns: AppBaseRecyclerViewAdapter<AllBoostAddOnsData>? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AllBoostAddonsFragment {
      val fragment = AllBoostAddonsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_all_boost_add_ons
  }

  override fun getViewModelClass(): Class<AddOnsViewModel> {
    return AddOnsViewModel::class.java
  }

  override fun onResume() {
    super.onResume()
    getBoostAddOnsData()
  }

  private fun getBoostAddOnsData() {
    viewModel?.getBoostAddOns(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? AllBoostAddOnsDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        if (adapterAddOns == null) {
          binding?.rvBoostAddOns?.apply {
            adapterAddOns = AppBaseRecyclerViewAdapter(baseActivity, setLastSeenData(response.data!!), this@AllBoostAddonsFragment)
            adapter = adapterAddOns
          }
        } else adapterAddOns?.notify(setLastSeenData(response.data!!))
      } else showShortToast(it.message())
    })
  }

  private fun setLastSeenData(data: ArrayList<AllBoostAddOnsData>): ArrayList<AllBoostAddOnsData> {
    val listAddOns = ManageBusinessData().getLastSeenData()
    if (listAddOns.isNotEmpty()) data.add(0, AllBoostAddOnsData(title = "Last Seen", manageBusinessList = listAddOns, isLastSeen = true))
    return data
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData().saveLastSeenData(data)
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let { businessAddOnsClick(it) }
      }
    }
  }

  private fun businessAddOnsClick(type: ManageBusinessData.BusinessType) {
    when (type) {
      ManageBusinessData.BusinessType.ic_project_terms_d -> {
      }
      ManageBusinessData.BusinessType.ic_digital_brochures_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_call_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_enquiries_d -> {
      }
      ManageBusinessData.BusinessType.ic_daily_business_update_d -> {
      }
      ManageBusinessData.BusinessType.ic_product_cataloge_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_testimonial_d -> {
      }
      ManageBusinessData.BusinessType.ic_business_keyboard_d -> {
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_search_icon_d, menu)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      searchView = searchItem.actionView as SearchView
      val searchAutoComplete = searchView?.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
      searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
      searchAutoComplete?.setTextColor(getColor(R.color.white))
      searchView?.queryHint = "Search boost add-ons"
      searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toUpperCase(Locale.ROOT)) }
          return false
        }
      })
    }
  }

  private fun startFilter(query: String) {

  }
}

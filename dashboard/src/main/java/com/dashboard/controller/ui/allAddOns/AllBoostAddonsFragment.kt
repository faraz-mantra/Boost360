package com.dashboard.controller.ui.allAddOns

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAllBoostAddOnsBinding
import com.dashboard.model.live.addOns.AllBoostAddOnsData
import com.dashboard.model.live.addOns.AllBoostAddOnsDataResponse
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

  override fun onCreateView() {
    super.onCreateView()
    viewModel?.getBoostAddOns(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? AllBoostAddOnsDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        binding?.rvBoostAddOns?.apply {
          adapterAddOns = AppBaseRecyclerViewAdapter(baseActivity, response.data!!, this@AllBoostAddonsFragment)
          adapter = adapterAddOns
        }
      } else showShortToast(it.message())
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

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

package com.appservice.ui.address.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentSearchLoactionBinding
import com.appservice.ui.address.adapter.PlacesRecentAdapter
import com.appservice.ui.address.adapter.PlacesResultAdapter
import com.appservice.ui.address.adapter.getRecentDataSearch
import com.appservice.ui.address.adapter.saveRecentDataSearch
import com.appservice.ui.address.startAddressFragmentActivity
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction

class SearchLocationFragment : AppBaseFragment<FragmentSearchLoactionBinding, BaseViewModel>() {

  private lateinit var placeAdapter: PlacesResultAdapter
  private lateinit var placeRecentAdapter: PlacesRecentAdapter

  private val mResultList: ArrayList<AutocompletePrediction>
    get() {
      return getRecentDataSearch() ?: arrayListOf()
    }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SearchLocationFragment {
      val fragment = SearchLocationFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_search_loaction
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    initializer()
    locationSearchAdapter()
  }

  private fun locationSearchAdapter() {
    binding?.rvRecentSearch?.isVisible = mResultList.isNotEmpty()
    binding?.txtRecentSearch?.isVisible = mResultList.isNotEmpty()
    placeRecentAdapter = PlacesRecentAdapter(baseActivity, mResultList) { prediction ->
      showShortToast(prediction.placeId)
    }
    binding?.rvRecentSearch?.adapter = placeRecentAdapter

    placeAdapter = PlacesResultAdapter(baseActivity, { isError, message ->
      binding?.cardLocationView?.isVisible = isError.not()
      message?.let { showShortToast(it) }
    }) { prediction ->
      saveRecentDataSearch(prediction)
      startAddressFragmentActivity(FragmentType.ADD_ADDRESS_VIEW, bundle = Bundle().apply { putParcelable(ADDRESS_DATA, prediction) })
    }
    binding?.rvLocationSearch?.adapter = placeAdapter
  }

  private fun initializer() {
    if (!Places.isInitialized()) Places.initialize(requireActivity(), resources.getString(R.string.google_map_key))
    binding?.etSearchBar?.afterTextChanged {
      if (it.isNotEmpty()) placeAdapter.filter.filter(it)
      binding?.imgClearTxt?.isVisible = it.isNotEmpty()
    }
    binding?.imgClearTxt?.setOnClickListener { binding?.etSearchBar?.setText("") }
  }
}
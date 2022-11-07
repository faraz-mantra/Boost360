package com.appservice.ui.staffs.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffListingBinding
import com.appservice.model.StatusKyc
import com.appservice.model.staffModel.DataItem
import com.appservice.model.staffModel.FilterBy
import com.appservice.model.staffModel.GetStaffListingRequest
import com.appservice.model.staffModel.GetStaffListingResponse
import com.appservice.model.staffModel.Result
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.model.serviceProduct.service.ServiceSearchListingResponse
import com.appservice.viewmodel.StaffViewModel
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.clientId
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.*
import com.inventoryorder.ui.tutorials.LearnHowItWorkBottomSheet
import java.util.*

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffViewModel>(), RecyclerItemClickListener, SearchView.OnQueryTextListener, AppOnZeroCaseClicked {

  private val TAG = "StaffProfileListingFrag"
  private var fragmentZeroCase: AppFragmentZeroCase? = null
  private val list: ArrayList<DataItem> = arrayListOf()
  private val finalList: ArrayList<DataItem> = arrayListOf()
  private var adapterStaff: AppBaseRecyclerViewAdapter<DataItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = "INR"
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null
  private var isServiceEmpty = false

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PAGE_START
  private var limit: Int = PAGE_SIZE
  private var isLastPageD = false
  private var isServiceAdd = false

  override fun getLayout(): Int {
    return R.layout.fragment_staff_listing
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  companion object {
    fun newInstance(): StaffProfileListingFragment {
      return StaffProfileListingFragment()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    Log.i(TAG, "onCreateView: ")
    setOnClickListener(binding?.serviceEmpty?.cbAddService, binding?.addStaffDoctor)
    val typeZeroCase = if (isStaffType) AppZeroCases.STAFF_LISTING else AppZeroCases.DOCTOR_PROFILE_LISTING
    this.fragmentZeroCase = AppRequestZeroCaseBuilder(typeZeroCase, this, baseActivity, !isLockStaff()).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, fragmentZeroCase, false)
    getBundleData()
    if (isLockStaff().not()) {
      nonEmptyView()
      layoutManagerN = LinearLayoutManager(baseActivity)
      WebEngageController.trackEvent(if (isDoctorClinic) DOCTOR_PROFILE_LIST else STAFF_PROFILE_LIST, PAGE_VIEW, NO_EVENT_VALUE)
      getListServiceFilterApi()
      layoutManagerN?.let { scrollPagingListener(it) }
      swipeRefreshListener()
      setOnClickListener(binding?.serviceEmpty?.cbAddService)
    } else emptyView()
  }

  private fun isLockStaff(): Boolean {
    return if (isDoctorClinic) (sessionLocal.getStoreWidgets()?.contains(StatusKyc.DOCTORBIO.name) == true).not()
    else (sessionLocal.getStoreWidgets()?.contains(StatusKyc.STAFFPROFILE.name) == true).not()
  }

  private fun checkIsAddNewStaff() {
    val b = arguments?.getBoolean(IntentConstant.IS_ADD_NEW.name) ?: false
    if (b) {
      startStaffFragmentActivity(
        if (isDoctorClinic) FragmentType.DOCTOR_ADD_EDIT_FRAGMENT else FragmentType.STAFF_DETAILS_FRAGMENT,
        clearTop = false, isResult = true
      )
    }
  }

  private fun getBundleData() {
    isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
    currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "INR"
    externalSourceId = arguments?.getString(IntentConstant.EXTERNAL_SOURCE_ID.name)
    applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
    userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
  }

  private fun getListServiceFilterApi() {
    showProgressN()
    viewModel?.getSearchListings(sessionLocal.fpTag, sessionLocal.fPID, "", 0, 1)?.observeOnce(viewLifecycleOwner) {
      isServiceEmpty = if ((it as? ServiceSearchListingResponse)?.result?.data.isNullOrEmpty().not()) {
        checkIsAddNewStaff()
        fetchStaffListing(isFirst = true, offSet = offSet, limit = limit)
        false
      } else {
        hideProgressN()
        setEmptyView(isEmpty = false, isServiceEmpty = true)
        true
      }
    }
  }

  private fun swipeRefreshListener() {
    binding?.mainlayout?.setOnRefreshListener {
      if (isServiceEmpty.not()) {
        binding?.mainlayout?.isRefreshing = true
        this.offSet = PAGE_START
        this.limit = PAGE_SIZE
        fetchStaffListing(isProgress = false, isFirst = true, offSet = offSet, limit = limit)
      } else binding?.mainlayout?.isRefreshing = false
    }
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.rvStaffList?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          adapterStaff?.addLoadingFooter(DataItem().getLoaderItem())
          offSet += limit
          fetchStaffListing(offSet = offSet, limit = limit)
        }
      }

      override val totalPageCount: Int
        get() = TOTAL_ELEMENTS
      override val isLastPage: Boolean
        get() = isLastPageD
      override val isLoading: Boolean
        get() = isLoadingD
    })
  }

  private fun fetchStaffListing(isProgress: Boolean = true, isFirst: Boolean = false, searchString: String = "", offSet: Int, limit: Int) {
    if ((isFirst || searchString.isNotEmpty()) && isProgress) showProgressN()
    viewModel?.getStaffList(getFilterRequest(offSet, limit))?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        setStaffDataItems((it as? GetStaffListingResponse)?.result, searchString.isNotEmpty(), isFirst)
      } else if (isFirst) showShortToast(it.errorMessage())
      if (isFirst || searchString.isNotEmpty()) hideProgressN()
    }
  }

  private fun setStaffDataItems(resultStaff: Result?, isSearchString: Boolean, isFirstLoad: Boolean) {
    val listStaff = resultStaff?.data
    if (isSearchString.not()) {
      if (isFirstLoad) finalList.clear()
      onStaffAddedOrUpdated(listStaff.isNullOrEmpty().not())
      if (listStaff.isNullOrEmpty().not()) {
        removeLoader()
        TOTAL_ELEMENTS = resultStaff?.paging?.count ?: 0
        finalList.addAll(listStaff!!)
        list.clear()
        list.addAll(finalList)
        isLastPageD = (finalList.size == TOTAL_ELEMENTS)
        setEmptyView(isEmpty = false)
        setAdapterNotify()
      } else if (isFirstLoad) setEmptyView(isEmpty = true)
    } else {
      if (listStaff.isNullOrEmpty().not()) {
        list.clear()
        list.addAll(listStaff!!)
        setAdapterNotify()
      }
    }
  }

  private fun onStaffAddedOrUpdated(b: Boolean) {
    val instance = FirestoreManager
    if (instance.getDrScoreData()?.metricdetail == null) return
    if (isDoctorClinic) instance.getDrScoreData()?.metricdetail?.boolean_create_doctor_e_profile = b
    else instance.getDrScoreData()?.metricdetail?.boolean_create_staff = b
    instance.updateDocument()
  }

  private fun setAdapterNotify() {
    if (adapterStaff == null) {
      adapterStaff = AppBaseRecyclerViewAdapter(baseActivity, list, this@StaffProfileListingFragment)
      binding?.rvStaffList?.layoutManager = layoutManagerN
      binding?.rvStaffList?.adapter = adapterStaff
      adapterStaff?.runLayoutAnimation(binding?.rvStaffList)
    } else adapterStaff?.notifyDataSetChanged()
  }

  private fun setEmptyView(isEmpty: Boolean, isServiceEmpty: Boolean = false) {
    when {
      isServiceEmpty -> {
        nonEmptyView()
        binding?.rvStaffList?.gone()
        binding?.addStaffDoctor?.gone()
        binding?.serviceEmpty?.root?.visible()
      }
      isEmpty -> emptyView()
      else -> nonEmptyView()
    }
  }

  private fun nonEmptyView() {
    binding?.childContainer?.gone()
    binding?.mainlayout?.visible()
    binding?.rvStaffList?.visible()
    binding?.serviceEmpty?.root?.gone()
    binding?.addStaffDoctor?.visible()
  }

  private fun emptyView() {
    binding?.mainlayout?.gone()
    binding?.addStaffDoctor?.gone()
    binding?.childContainer?.visible()
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterStaff?.removeLoadingFooter()
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val staff = item as DataItem
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staff)
    startStaffFragmentActivity(FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT, bundle, clearTop = false, isResult = true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_stafflisting, menu)
    val searchView = menu.findItem(R.id.app_bar_search).actionView as? SearchView
    val searchAutoComplete = searchView?.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
    searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
    searchAutoComplete?.setTextColor(getColor(R.color.white))
    searchView?.queryHint = getString(R.string.search_staff)
    searchView?.setOnQueryTextListener(this)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_help -> {
        openHelpBottomSheet()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun openHelpBottomSheet() {
    val sheet = LearnHowItWorkBottomSheet()
    sheet.show(parentFragmentManager, LearnHowItWorkBottomSheet::class.java.name)
  }

  override fun onClick(v: View) {
    when (v) {
      binding?.serviceEmpty?.cbAddService -> {
        isServiceAdd = true
        startFragmentActivity(FragmentType.SERVICE_DETAIL_VIEW, bundle = sendBundleData(), isResult = true)
      }
      binding?.addStaffDoctor -> {
        WebEngageController.trackEvent(if (isDoctorClinic) ADD_DOCTOR_PROFILE else ADD_STAFF_PROFILE, CLICK, NO_EVENT_VALUE)
        startStaffFragmentActivity(
          if (isDoctorClinic) FragmentType.DOCTOR_ADD_EDIT_FRAGMENT else FragmentType.STAFF_DETAILS_FRAGMENT,
          clearTop = false,
          isResult = true
        )
      }
    }
  }

  private fun sendBundleData(): Bundle {
    val bundle = Bundle()
    bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name, isNonPhysicalExperience ?: false)
    bundle.putString(IntentConstant.CURRENCY_TYPE.name, currencyType)
    bundle.putString(IntentConstant.FP_ID.name, sessionLocal.fPID)
    bundle.putString(IntentConstant.FP_TAG.name, sessionLocal.fpTag)
    bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
    bundle.putString(IntentConstant.CLIENT_ID.name, clientId)
    bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name, externalSourceId)
    bundle.putString(IntentConstant.APPLICATION_ID.name, applicationId)
    return bundle
  }

  override fun onQueryTextSubmit(query: String?): Boolean {
    return false
  }

  override fun onQueryTextChange(query: String?): Boolean {
    if (query.isNullOrEmpty().not()) filterStaff(query!!)
    return false
  }

  private fun filterStaff(query: String) {
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) {
        this.offSet = PAGE_START
        this.limit = PAGE_SIZE
        if (isServiceAdd) getListServiceFilterApi()
        else fetchStaffListing(isFirst = true, offSet = offSet, limit = limit)
      }
    }
  }

  fun showProgressN() {
    binding?.progress?.visible()
  }

  fun hideProgressN() {
    binding?.mainlayout?.isRefreshing = false
    binding?.progress?.gone()
  }

  fun getFilterRequest(offSet: Int, limit: Int): GetStaffListingRequest {
    return GetStaffListingRequest(FilterBy(offset = offSet, limit = limit), sessionLocal.fpTag, "")
  }

  override fun primaryButtonClicked() {
    if (isLockStaff().not()) {
      WebEngageController.trackEvent(if (isDoctorClinic) ADD_DOCTOR_PROFILE else ADD_STAFF_PROFILE, CLICK, NO_EVENT_VALUE)
      val type = if (isDoctorClinic) FragmentType.DOCTOR_ADD_EDIT_FRAGMENT else FragmentType.STAFF_DETAILS_FRAGMENT
      startStaffFragmentActivity(type, isResult = true)
    } else startStorePage(if (isDoctorClinic) StatusKyc.DOCTORBIO.name else "${StatusKyc.STAFFPROFILE.name}15")
  }

  override fun secondaryButtonClicked() {
    if (isDoctorClinic) showShortToast(getString(R.string.coming_soon)) else openHelpBottomSheet()
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }
}


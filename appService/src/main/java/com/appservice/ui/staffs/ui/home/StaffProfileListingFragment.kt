package com.appservice.ui.staffs.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.model.staffModel.*
import com.appservice.ui.staffs.ui.UserSession
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.model.ServiceSearchListingResponse
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.firestore.FirestoreManager
import com.framework.pref.Key_Preferences
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases
import com.framework.webengageconstant.*
import com.inventoryorder.ui.tutorials.LearnHowItWorkBottomSheet
import kotlinx.android.synthetic.main.fragment_staff_listing.*
import kotlinx.android.synthetic.main.fragment_staff_profile.view.*
import java.util.*

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffViewModel>(),
  RecyclerItemClickListener, SearchView.OnQueryTextListener, OnZeroCaseClicked {

  private val TAG = "StaffProfileListingFrag"
  private var fragmentZeroCase: FragmentZeroCase?=null
  private val list: ArrayList<DataItem> = arrayListOf()
  private val finalList: ArrayList<DataItem> = arrayListOf()
  private var adapterStaff: AppBaseRecyclerViewAdapter<DataItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = "INR"
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null
  private lateinit var menuAdd: MenuItem
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
    getBundleData()
    if (isLockStaff().not()) {
      layoutManagerN = LinearLayoutManager(baseActivity)
      WebEngageController.trackEvent(STAFF_PROFILE_LIST, PAGE_VIEW, NO_EVENT_VALUE)
      getListServiceFilterApi()
      layoutManagerN?.let { scrollPagingListener(it) }
      swipeRefreshListener()
      setOnClickListener(
        binding?.serviceEmpty?.cbAddService
      )
    }
    this.fragmentZeroCase = RequestZeroCaseBuilder(ZeroCases.STAFF_LISTING, this, baseActivity).getRequest().build()
  }

  private fun isLockStaff(): Boolean {
    return if (sessionLocal.getStoreWidgets()?.contains(StatusKyc.STAFFPROFILE.name) == true) {
      binding?.staffListSwipeRefresh?.visible()
      binding?.staffLock?.root?.gone()
      false
    } else {
      binding?.staffListSwipeRefresh?.gone()
      binding?.staffLock?.root?.visible()
      binding?.staffLock?.btnStaffAddOns?.setOnClickListener(this)
      true
    }
  }

  private fun checkIsAddNewStaff() {
    val b = arguments?.getBoolean(IntentConstant.IS_ADD_NEW.name) ?: false
    if (b) {
      WebEngageController.trackEvent(ADD_STAFF_PROFILE, CLICK, NO_EVENT_VALUE)
      startStaffFragmentActivity(
        FragmentType.STAFF_DETAILS_FRAGMENT,
        clearTop = false,
        isResult = true
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
    viewModel?.getSearchListings(UserSession.fpTag, UserSession.fpId, "", 0, 1)
      ?.observeOnce(viewLifecycleOwner, {
        val data = (it as? ServiceSearchListingResponse)?.result?.data
        Log.i(TAG, "getListServiceFilterApi: "+data?.size)
        if (data.isNullOrEmpty().not()) {
          checkIsAddNewStaff()
          fetchStaffListing(isFirst = true, offSet = offSet, limit = limit)
          isServiceEmpty = false
        } else {
          hideProgressN()
          setEmptyView(isStaffEmpty = false, isServiceEmpty = true)
          isServiceEmpty = true
        }
      })
  }

  private fun swipeRefreshListener() {
    binding?.staffListSwipeRefresh?.setOnRefreshListener {
      if (isServiceEmpty.not()) {
        binding?.staffListSwipeRefresh?.isRefreshing = true
        this.offSet = PAGE_START
        this.limit = PAGE_SIZE
        fetchStaffListing(isProgress = false, isFirst = true, offSet = offSet, limit = limit)
      } else binding?.staffListSwipeRefresh?.isRefreshing = false
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

  private fun fetchStaffListing(
    isProgress: Boolean = true,
    isFirst: Boolean = false,
    searchString: String = "",
    offSet: Int,
    limit: Int
  ) {
    if ((isFirst || searchString.isNotEmpty()) && isProgress) showProgressN()
    viewModel?.getStaffList(getFilterRequest(offSet, limit))?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        setStaffDataItems(
          (it as? GetStaffListingResponse)?.result,
          searchString.isNotEmpty(),
          isFirst
        )
      } else if (isFirst) showShortToast(it.errorMessage())
      if (isFirst || searchString.isNotEmpty()) hideProgressN()
    })
  }

  private fun setStaffDataItems(
    resultStaff: Result?,
    isSearchString: Boolean,
    isFirstLoad: Boolean
  ) {
    val listStaff = resultStaff?.data
    Log.i(TAG, "setStaffDataItems: "+listStaff?.size)

    if (isSearchString.not()) {
      if (isFirstLoad) finalList.clear()
      onStaffAddedOrUpdated(listStaff.isNullOrEmpty().not())
      if (listStaff.isNullOrEmpty().not()) {
        removeLoader()
        setEmptyView(false)
        TOTAL_ELEMENTS = resultStaff?.paging?.count ?: 0
        finalList.addAll(listStaff!!)
        list.clear()
        list.addAll(finalList)
        isLastPageD = (finalList.size == TOTAL_ELEMENTS)
        setAdapterNotify()
      } else if (isFirstLoad) setEmptyView(true)
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
    instance.getDrScoreData()?.metricdetail?.boolean_create_staff = b
    instance.updateDocument()
  }

  private fun setAdapterNotify() {
    if (adapterStaff == null) {
      adapterStaff =
        AppBaseRecyclerViewAdapter(baseActivity, list, this@StaffProfileListingFragment)
      binding?.rvStaffList?.layoutManager = layoutManagerN
      binding?.rvStaffList?.adapter = adapterStaff
      adapterStaff?.runLayoutAnimation(binding?.rvStaffList)
    } else adapterStaff?.notifyDataSetChanged()
  }

  private fun setEmptyView(isStaffEmpty: Boolean, isServiceEmpty: Boolean = false) {
    Log.i(TAG, "setEmptyView: "+isStaffEmpty+" "+isServiceEmpty)
    if (isStaffEmpty)
    addFragment(containerID = R.id.container, fragmentZeroCase!!, true)
    else removeZeroCase()
    binding?.rvStaffList?.visibility =
      if (isStaffEmpty || isServiceEmpty) View.GONE else View.VISIBLE
    if (this::menuAdd.isInitialized) menuAdd.isVisible = isServiceEmpty.not()
  }

  private fun removeZeroCase() {
   /* parentFragmentManager.popBackStack()
    parentFragmentManager.beginTransaction().detach(fragmentZeroCase!!).commit()*/
    removeFragment(FragmentZeroCase::class.java.name)
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
    startStaffFragmentActivity(
      FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT,
      bundle,
      clearTop = false,
      isResult = true
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_stafflisting, menu)
    val searchView = menu.findItem(R.id.app_bar_search).actionView as? SearchView
    val searchAutoComplete =
      searchView?.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
    searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
    searchAutoComplete?.setTextColor(getColor(R.color.white))
    searchView?.queryHint = getString(R.string.search_staff)
    searchView?.setOnQueryTextListener(this)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    menuAdd = menu.findItem(R.id.menu_add_staff)
    val b = sessionLocal.getStoreWidgets()?.contains(StatusKyc.STAFFPROFILE.name) ?: false
    menuAdd.isVisible = b
    if (b) menuAdd.isVisible = isServiceEmpty.not()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add_staff -> {
        WebEngageController.trackEvent(ADD_STAFF_PROFILE, CLICK, NO_EVENT_VALUE)
        startStaffFragmentActivity(
          FragmentType.STAFF_DETAILS_FRAGMENT,
          clearTop = false,
          isResult = true
        )
        true
      }
      R.id.app_bar_search -> {
        true
      }
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
        startFragmentActivity(
          FragmentType.SERVICE_DETAIL_VIEW,
          bundle = sendBundleData(),
          isResult = true
        )
      }
      binding?.staffLock?.btnStaffAddOns -> startStorePage()
    }
  }

  private fun sendBundleData(): Bundle {
    val bundle = Bundle()
    bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name, isNonPhysicalExperience ?: false)
    bundle.putString(IntentConstant.CURRENCY_TYPE.name, currencyType)
    bundle.putString(IntentConstant.FP_ID.name, UserSession.fpId)
    bundle.putString(IntentConstant.FP_TAG.name, UserSession.fpTag)
    bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
    bundle.putString(IntentConstant.CLIENT_ID.name, UserSession.clientId)
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
        Log.i(TAG, "onActivityResult: "+isServiceAdd)
        if (isServiceAdd){
          getListServiceFilterApi()
        }
        else{
          fetchStaffListing(isFirst = true, offSet = offSet, limit = limit)
        }
      }
    }
  }

  fun showProgressN() {
    binding?.progress?.visible()
  }

  fun hideProgressN() {
    binding?.staffListSwipeRefresh?.isRefreshing = false
    binding?.progress?.gone()
  }


  private fun startStorePage() {
    try {
      showProgress("Loading. Please wait...")
      val intent = Intent(baseActivity, Class.forName("com.boost.upgrades.UpgradeActivity"))
      intent.putExtra("expCode", sessionLocal.fP_AppExperienceCode)
      intent.putExtra("fpName", sessionLocal.fpTag)
      intent.putExtra("fpid", sessionLocal.fPID)
      intent.putExtra("fpTag", sessionLocal.fpTag)
      intent.putExtra(
        "accountType",
        sessionLocal.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)
      )
      intent.putStringArrayListExtra(
        "userPurchsedWidgets",
        ArrayList(sessionLocal.getStoreWidgets() ?: ArrayList())
      )
      intent.putExtra("email", sessionLocal.fPEmail ?: "ria@nowfloats.com")
      intent.putExtra("mobileNo", sessionLocal.fPPrimaryContactNumber ?: "9160004303")
      intent.putExtra("profileUrl", sessionLocal.fPLogo)
      intent.putExtra("buyItemKey", "${StatusKyc.STAFFPROFILE.name}15")// feature key
      baseActivity.startActivity(intent)
      Handler(Looper.getMainLooper()).postDelayed({ hideProgress() }, 1000)
    } catch (e: Exception) {
      showLongToast("Unable to start upgrade activity.")
    }
  }

  override fun primaryButtonClicked() {
    startStaffFragmentActivity(
      FragmentType.STAFF_DETAILS_FRAGMENT,
      clearTop = false,
      isResult = true)
  }

  override fun secondaryButtonClicked() {
    openHelpBottomSheet()
  }

  override fun ternaryButtonClicked() {
    TODO("Not yet implemented")
  }

  override fun onBackPressed() {
    baseActivity.finishAfterTransition()
  }

}

fun getFilterRequest(offSet: Int, limit: Int): GetStaffListingRequest {
  return GetStaffListingRequest(FilterBy(offset = offSet, limit = limit), UserSession.fpTag, "")
}


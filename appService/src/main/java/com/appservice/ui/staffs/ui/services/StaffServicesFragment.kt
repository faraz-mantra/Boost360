package com.appservice.ui.staffs.ui.services

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentSelectServicesBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.model.staffModel.*
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.viewmodel.StaffViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import java.util.*

class StaffServicesFragment : AppBaseFragment<FragmentSelectServicesBinding, StaffViewModel>(), RecyclerItemClickListener {

  private var isEdit: Boolean? = null
  var data: ArrayList<DataItemService?> = arrayListOf()
  var adapterServices: AppBaseRecyclerViewAdapter<DataItemService>? = null
  private var listServices: ArrayList<DataItemService>? = null
  private var serviceIds: ArrayList<String>? = null
  private var layoutManagerN: LinearLayoutManager? = null

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var TOTAL_COUNT = 0
  private var offSet: Int = PaginationScrollListener.PAGE_START
  private var limit: Int = PaginationScrollListener.PAGE_SIZE
  private var isLastPageD = false

  companion object {
    fun newInstance(): StaffServicesFragment {
      return StaffServicesFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_select_services
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    sessionLocal = UserSessionManager(requireActivity())
    init()
    getBundleData()
  }

  private fun init() {
    layoutManagerN = LinearLayoutManager(baseActivity)
    binding?.ctvHeading?.text = if (isDoctorClinic) getString(R.string.select_what_services_that_the_doctor_will_provide) else getString(R.string.select_what_services_that_the_staff_will_provide)
    binding?.ctvSubheading?.text = if (isDoctorClinic) getString(R.string.doctor_service_customer_know_provide_service_looking_for) else getString(R.string.staff_service_customer_know_provide_service_looking_for)
    setOnClickListener(binding?.flConfirmServices)
    loadData(isFirst = true, offSet = offSet, limit = limit)
    layoutManagerN?.let { scrollPagingListener(it) }
  }

  private fun getBundleData() {
    if (listServices == null) listServices = arrayListOf()
    serviceIds = arguments?.getStringArrayList(IntentConstant.STAFF_SERVICES.name)
    isEdit = serviceIds.isNullOrEmpty().not()
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.rvServiceProvided?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          adapterServices?.addLoadingFooter(DataItemService().getLoaderItem())
          offSet += limit
          loadData(offSet = offSet, limit = limit)
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

  private fun loadData(isFirst: Boolean = false, offSet: Int? = null, limit: Int? = null) {
    if (isFirst) showProgress(getString(R.string.loading_))
    val request = ServiceListRequest(FilterBy("ALL", limit, offSet), "", floatingPointTag = sessionLocal.fpTag)
    viewModel?.getServiceListing(request)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()){
        val response = (it as? ServiceListResponse)?.result
        setDataServices(response, isFirst)
      } else if (isFirst) showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      hideProgress()
    }
  }

  private fun setDataServices(response: ResultService?, isFirstLoad: Boolean) {
    val serviceListData = response?.data
    TOTAL_COUNT = response?.paging?.count?:0
    if (isFirstLoad) data.clear()
    if (serviceListData.isNullOrEmpty().not()) {
      removeLoader()
      data.addAll(serviceListData!!)
      TOTAL_ELEMENTS = data.size
      isLastPageD = false
      setAdapterNotify()
    }else{
      if (!isFirstLoad){
        removeLoader()
        isLastPageD = true
      }
    }
  }

  private fun setAdapterNotify() {
    setServiceCount()
    if (adapterServices == null) {
      binding?.rvServiceProvided?.apply {
        adapterServices = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItemService>, itemClickListener = this@StaffServicesFragment)
        layoutManager = layoutManagerN
        adapter = adapterServices
      }
    } else adapterServices?.notifyDataSetChanged()

    when (isEdit) {
      true -> {
        data.forEach { datum ->
          if (serviceIds?.contains(datum?.id) == true) {
            datum?.isChecked = true
            val matchedRecords = listServices?.filter { (it.id == datum?.id) }
            if (matchedRecords?.isNullOrEmpty() == true){
              listServices?.add(datum!!)
            }
          }
        }
      }
    }
    setServiceCount()
    adapterServices?.notifyDataSetChanged()
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val dataItem = item as DataItemService
    when (dataItem.isChecked) {
      true -> {
        dataItem.isChecked = false
      }
      else -> {
        dataItem.isChecked = true
      }
    }
    when (dataItem.isChecked) {
      true -> {
        val matchedRecords = listServices?.filter { (it.id == dataItem.id) }
        if (matchedRecords?.isNullOrEmpty() == true){
          listServices?.add(dataItem)
        }
      }
      false -> {
        val matchedRecords = listServices?.filter { (it.id == dataItem.id) }
        if (matchedRecords?.isNullOrEmpty() == false){
          listServices?.remove(dataItem)
        }
      }
    }
    setServiceCount()

  }

  private fun setServiceCount() {
    val serviceMsg = getString(R.string.service_selected)
    val servicesMsg = getString(R.string.services_selected)
    val serviceButtonText = getString(R.string.service).lowercase(Locale.getDefault())
    val servicesButtonText = getString(R.string.services).lowercase(Locale.getDefault())
    var serviceCount = ""
    var serviceConfirm = ""
    when {
      listServices?.size ?: 0 < 2 -> {
        serviceCount = "${listServices?.size}/${data.size} $serviceMsg"
        serviceConfirm = "Confirm ${listServices?.size} $serviceButtonText"
      }
      else -> {
        serviceCount = "${listServices?.size}/${data.size} $servicesMsg"
        serviceConfirm = "Confirm ${listServices?.size} $servicesButtonText"
      }
    }

    binding?.ctvServicesCountTitle?.text = serviceCount
    binding?.ctvServicesCount?.text = serviceConfirm
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.flConfirmServices -> {
        val intent = Intent()
        intent.putExtra(IntentConstant.STAFF_SERVICES.name, listServices)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
        baseActivity.finish()
      }
    }
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterServices?.removeLoadingFooter()
    }
  }
}

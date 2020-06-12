package com.inventoryorder.ui.consultation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentVideoConsultBinding
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequestItem
import com.inventoryorder.model.orderfilter.QueryObject
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.PaginationScrollListener
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.order.InventoryOrderListResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.bottomsheet.FilterBottomSheetDialog
import com.inventoryorder.ui.startFragmentActivity
import com.inventoryorder.utils.copyClipBoard
import com.inventoryorder.utils.openWebPage
import java.util.*
import kotlin.collections.ArrayList

class VideoConsultFragment : BaseInventoryFragment<FragmentVideoConsultBinding>(), RecyclerItemClickListener {

  private lateinit var requestFilter: OrderFilterRequest
  private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
  private var orderList = ArrayList<OrderItem>()
  private var orderListFilter = ArrayList<OrderItem>()
  private var layoutManager: LinearLayoutManager? = null
  private var filterItem: FilterModel? = null
  private var filterList: ArrayList<FilterModel> = FilterModel().getDataConsultations()

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var currentPage = PAGE_START
  private var isLastPageD = false

  companion object {
    fun newInstance(bundle: Bundle? = null): VideoConsultFragment {
      val fragment = VideoConsultFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnAdd)
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
    getSellerOrdersFilterApi(getRequestFilterData(arrayListOf()), isFirst = true)
  }

  private fun getSellerOrdersFilterApi(request: OrderFilterRequest, isFirst: Boolean = false, isRefresh: Boolean = false) {
    if (isFirst) binding?.progress?.visible()
    viewModel?.getSellerOrdersFilter(auth, request)?.observeOnce(viewLifecycleOwner, Observer {
      binding?.progress?.gone()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = (it as? InventoryOrderListResponse)?.Data
        if (isRefresh) orderList.clear()
        if (response == null) {
          orderAdapter?.notify(ArrayList())
          return@Observer
        }
        binding?.bookingRecycler?.visible()
        val list = (response.Items ?: ArrayList()).map { item ->
          item.recyclerViewType = RecyclerViewItemType.VIDEO_CONSULT_ITEM_TYPE.getLayout();item
        } as ArrayList<OrderItem>
        TOTAL_ELEMENTS = response.total()
        orderList.addAll(list)
        isLastPageD = (orderList.size == TOTAL_ELEMENTS)
        if (isFirst.not() && orderAdapter != null) {
          orderAdapter?.removeLoadingFooter()
          isLoadingD = false
          orderAdapter?.notify(getDateWiseFilter(orderList))
        } else setAdapterBookingList(getDateWiseFilter(orderList))
      } else {
        binding?.bookingRecycler?.gone()
        showShortToast(it.message)
      }
    })
  }

  private fun getDateWiseFilter(orderList: ArrayList<OrderItem>): ArrayList<OrderItem> {
    val list = ArrayList<OrderItem>()
    val mapList = TreeMap<Date, ArrayList<OrderItem>>(Collections.reverseOrder())
    orderList.forEach { it.stringToDate()?.let { it1 -> setItem(mapList, it, it1) } }
    for ((key, value) in mapList) {
      list.add(OrderItem().getDateObject(key))
      list.addAll(value)
    }
    return list
  }

  private fun setItem(mapList: TreeMap<Date, ArrayList<OrderItem>>, it: OrderItem, key: Date) {
    if (mapList.containsKey(key).not()) mapList[key] = arrayListOf(it)
    else {
      val listItem = mapList[key]
      listItem?.add(it)
      listItem?.let { it1 -> mapList.put(key, it1) }
    }
  }

  private fun setAdapterBookingList(list: ArrayList<OrderItem>) {
    binding?.bookingRecycler?.post {
      orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.bookingRecycler?.layoutManager = layoutManager
      binding?.bookingRecycler?.adapter = orderAdapter
      binding?.bookingRecycler?.let { orderAdapter?.runLayoutAnimation(it) }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAdd -> startFragmentActivity(FragmentType.CREATE_NEW_BOOKING, Bundle())
    }
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.bookingRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && binding?.btnAdd?.visibility === View.VISIBLE) binding?.btnAdd?.hide()
        else if (dy < 0 && binding?.btnAdd?.visibility !== View.VISIBLE) binding?.btnAdd?.show()
      }

      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          currentPage += requestFilter.limit ?: 0
          orderAdapter?.addLoadingFooter(OrderItem().getLoaderItem())
          requestFilter.skip = currentPage
          getSellerOrdersFilterApi(requestFilter)
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

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_filter -> {
        filterBottomSheet()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun filterBottomSheet() {
    val filterSheet = FilterBottomSheetDialog()
    filterSheet.onDoneClicked = { clickFilterItem(it) }
    filterSheet.setList(filterList)
    filterSheet.show(this.parentFragmentManager, FilterBottomSheetDialog::class.java.name)
  }

  private fun clickFilterItem(item: FilterModel?) {
    this.filterItem = item
    when (this.filterItem?.type?.let { FilterModel.FilterType.fromType(it) }) {
      FilterModel.FilterType.ALL_CONSULTATIONS -> getSellerOrdersFilterApi(getRequestFilterData(arrayListOf()), isFirst = true, isRefresh = true)
      FilterModel.FilterType.COMPLETED_CONSULTATIONS -> {
        val status = arrayListOf(OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name,
            OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name)
        getSellerOrdersFilterApi(getRequestFilterData(status), isFirst = true, isRefresh = true)
      }
      FilterModel.FilterType.CANCEL_CONSULTATIONS -> getSellerOrdersFilterApi(getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name)), isFirst = true, isRefresh = true)
      else -> getSellerOrdersFilterApi(getRequestFilterData(arrayListOf()), isFirst = true, isRefresh = true)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.queryHint = resources.getString(R.string.queryHintVideo)
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toLowerCase()) }
          return false
        }
      })
    }
  }


  private fun startFilter(query: String) {
    if (query.isEmpty().not()) {
      orderListFilter.clear()
      orderListFilter.addAll(orderList)
      orderListFilter.let { it1 ->
        val list = it1.filter {
          it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query) || it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query)
        } as ArrayList<OrderItem>
        orderAdapter?.notify(getDateWiseFilter(list))
      }
    } else orderAdapter?.notify(getDateWiseFilter(orderList))
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.VIDEO_CONSULT_ITEM_CLICKED.ordinal -> {
        val orderItem = item as? OrderItem
        val bundle = Bundle()
        bundle.putString(IntentConstant.ORDER_ID.name, orderItem?._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentActivity(FragmentType.VIDEO_CONSULT_DETAIL_VIEW, bundle, isResult = true)
      }
      RecyclerViewActionType.VIDEO_CONSULT_CALL_CLICKED.ordinal -> videoConsultCall((item as? OrderItem))
      RecyclerViewActionType.VIDEO_CONSULT_COPY_CLICKED.ordinal -> videoConsultCopy((item as? OrderItem))
    }
  }

  private fun videoConsultCall(order: OrderItem?) {
    order?.consultationWindowUrl()?.let {
      if (baseActivity.openWebPage(it).not()) showLongToast(resources.getString(R.string.error_opening_consultation_window))
    }
  }

  private fun videoConsultCopy(order: OrderItem?) {
    order?.consultationJoiningUrl()?.let {
      if (baseActivity.copyClipBoard(it)) showLongToast(resources.getString(R.string.copied_patient_url))
      else showLongToast(resources.getString(R.string.error_copied_patient_url))
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val isRefresh = bundle?.getBoolean(IntentConstant.IS_REFRESH.name)
      if (isRefresh != null && isRefresh) clickFilterItem(filterItem)
    }
  }


  private fun getRequestFilterData(statusList: ArrayList<String>): OrderFilterRequest {
    currentPage = PAGE_START
    requestFilter = OrderFilterRequest(clientId = clientId, skip = currentPage, limit = PAGE_SIZE)
    requestFilter.filterBy.add(OrderFilterRequestItem(QueryConditionType = OrderFilterRequestItem.Condition.AND.name, QueryObject = getQueryList()))
    requestFilter.filterBy.add(OrderFilterRequestItem(QueryConditionType = OrderFilterRequestItem.Condition.OR.name, QueryObject = getQueryStatusList(statusList)))
    return requestFilter
  }

  private fun getQueryList(): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    queryList.add(QueryObject(QueryObject.QueryKey.Identifier.value, fpTag, QueryObject.Operator.EQ.name))
    queryList.add(QueryObject(QueryObject.QueryKey.Mode.value, OrderSummaryRequest.OrderMode.APPOINTMENT.name, QueryObject.Operator.EQ.name))
    queryList.add(QueryObject(QueryObject.QueryKey.DeliveryMode.value, OrderSummaryRequest.DeliveryMode.ONLINE.name, QueryObject.Operator.EQ.name))
    queryList.add(QueryObject(QueryObject.QueryKey.DeliveryProvider.value, QueryObject.QueryValue.NF_VIDEO_CONSULATION.name, QueryObject.Operator.EQ.name))
    return queryList
  }

  private fun getQueryStatusList(statusList: ArrayList<String>): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    statusList.forEach { queryList.add(QueryObject(QueryObject.QueryKey.Status.value, it, QueryObject.Operator.EQ.name)) }
    return queryList
  }
}
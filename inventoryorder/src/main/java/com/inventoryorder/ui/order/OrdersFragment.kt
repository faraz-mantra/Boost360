package com.inventoryorder.ui.order

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
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
import com.inventoryorder.databinding.FragmentOrdersBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequestItem
import com.inventoryorder.model.orderfilter.QueryObject
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.PaginationScrollListener
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.inventoryorder.rest.response.order.InventoryOrderListResponse
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.util.*
import kotlin.collections.ArrayList

open class OrdersFragment : BaseInventoryFragment<FragmentOrdersBinding>(), RecyclerItemClickListener {

  lateinit var mPopupWindow: PopupWindow
  private lateinit var requestFilter: OrderFilterRequest
  private var typeAdapter: AppBaseRecyclerViewAdapter<OrderSummaryModel>? = null
  private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
  private var typeList: ArrayList<OrderSummaryModel>? = null
  private var orderList = ArrayList<OrderItem>()
  private var orderListFinalList = ArrayList<OrderItem>()
  private var layoutManagerN: LinearLayoutManager? = null
  private var orderItem: OrderItem? = null
  private var position: Int? = null

  /* Paging */
  private var isLoadingD = false
  private var isSerchItem = false
  private var TOTAL_ELEMENTS = 0
  private var currentPage = PAGE_START
  private var isLastPageD = false
  private var orderItemType = OrderSummaryModel.OrderSummaryType.TOTAL.type

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): OrdersFragment {
      val fragment = OrdersFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent("Clicked on Orders", "ORDERS", it) }
    //setOnClickListener(binding?.btnAdd)
    apiSellerSummary()
    layoutManagerN = LinearLayoutManager(baseActivity)
    layoutManagerN?.let { scrollPagingListener(it) }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
//      binding?.btnAdd -> {
//        startFragmentOrderActivity(FragmentType.CREATE_NEW_ORDER, Bundle())
//      }
    }
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.orderRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD && !isSerchItem) {
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

  private fun apiSellerSummary() {
    binding?.progress?.visible()
    viewModel?.getSellerSummary(clientId, fpTag)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        errorOnSummary(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it as? OrderSummaryResponse
        setToolbarTitle(resources.getString(R.string.orders) + " (${response?.Data?.TotalOrders ?: 0})")
        typeList = response?.Data?.getOrderType()
        typeList?.let { it1 -> setAdapterSellerSummary(it1) } ?: errorOnSummary(null)
      } else errorOnSummary(it?.message)
    })
  }

  private fun setAdapterOrderList(list: ArrayList<OrderItem>) {
    binding?.orderRecycler?.apply {
      orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this@OrdersFragment)
      layoutManager = layoutManagerN
      adapter = orderAdapter
      orderAdapter?.runLayoutAnimation(this)
    }
  }

  private fun setAdapterSellerSummary(typeList: ArrayList<OrderSummaryModel>) {
    binding?.typeRecycler?.visible()
    binding?.viewShadow?.visible()
    orderList.clear()
    orderListFinalList.clear()
    requestFilter = getRequestFilterData(arrayListOf())
    getSellerOrdersFilterApi(requestFilter, isFirst = true)
    binding?.typeRecycler?.post {
      typeAdapter = AppBaseRecyclerViewAdapter(baseActivity, typeList, this)
      binding?.typeRecycler?.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.typeRecycler?.adapter = typeAdapter
      binding?.typeRecycler?.let { typeAdapter?.runLayoutAnimation(it) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal -> {
        val orderItem = item as? OrderItem
        val bundle = Bundle()
        bundle.putString(IntentConstant.ORDER_ID.name, orderItem?._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentOrderActivity(FragmentType.ORDER_DETAIL_VIEW, bundle, isResult = true)
      }
      RecyclerViewActionType.ORDER_SUMMARY_CLICKED.ordinal -> {
        val orderItem = item as? OrderSummaryModel
        typeList?.forEach { it.isSelected = (it.type == orderItem?.type) }
        typeAdapter?.notifyDataSetChanged()
        orderItemType = orderItem?.type ?: OrderSummaryModel.OrderSummaryType.TOTAL.type
        loadNewData()
      }
      RecyclerViewActionType.ORDER_BUTTON_CLICKED.ordinal -> {
        this.position = position
        this.orderItem = (item as? OrderItem)
        this.orderItem?.orderBtnStatus()?.firstOrNull()?.let { clickActionOrderButton(it, this.orderItem!!) }
      }
      RecyclerViewActionType.ORDER_DROPDOWN_CLICKED.ordinal -> {
        if (::mPopupWindow.isInitialized && mPopupWindow.isShowing) mPopupWindow.dismiss()
        val orderMenu = OrderMenuModel.MenuStatus.from((item as? OrderMenuModel)?.type) ?: return
        clickActionOrderButton(orderMenu, this.orderItem!!)
      }
    }
  }

  override fun onItemClickView(position: Int, view: View, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal -> popUpMenuButton(position, view, (item as? OrderItem))
    }
  }

  private fun popUpMenuButton(position: Int, view: View, orderItem: OrderItem?) {
    this.orderItem = orderItem
    this.position = position
    val list = OrderMenuModel().getOrderMenu(orderItem)
    if (list.isNotEmpty()) list.removeAt(0)
    val orderMenuView: View = LayoutInflater.from(baseActivity).inflate(R.layout.menu_order_button, null)
    val rvOrderMenu: RecyclerView? = orderMenuView.findViewById(R.id.rv_menu_order)
    rvOrderMenu?.apply {
      val adapterMenu = AppBaseRecyclerViewAdapter(baseActivity, list, this@OrdersFragment)
      adapter = adapterMenu
    }
    mPopupWindow = PopupWindow(orderMenuView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    mPopupWindow.showAsDropDown(view, 0, 0)
  }

  private fun clickActionOrderButton(orderMenu: OrderMenuModel.MenuStatus, orderItem: OrderItem) {
    when (orderMenu) {
      OrderMenuModel.MenuStatus.CONFIRM_ORDER -> {
        val sheetConfirm = ConfirmBottomSheetDialog()
        sheetConfirm.setData(orderItem)
        sheetConfirm.onClicked = { apiConfirmOrder(it) }
        sheetConfirm.show(this.parentFragmentManager, ConfirmBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
        val sheetRequestPayment = RequestPaymentBottomSheetDialog()
        sheetRequestPayment.setData(orderItem)
        sheetRequestPayment.onClicked = {
          showProgress()
          sendPaymentLinkOrder(getString(R.string.payment_request_send))
        }
        sheetRequestPayment.show(this.parentFragmentManager, RequestPaymentBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.CANCEL_ORDER -> {
        val sheetCancel = CancelBottomSheetDialog()
        sheetCancel.setData(orderItem)
        sheetCancel.onClicked = this@OrdersFragment::apiCancelOrder
        sheetCancel.show(this.parentFragmentManager, CancelBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> markCodPaymentRequest()
      OrderMenuModel.MenuStatus.MARK_AS_DELIVERED -> {
        val sheetDelivered = DeliveredBottomSheetDialog()
        sheetDelivered.setData(orderItem)
        sheetDelivered.onClicked = { deliveredOrder(it) }
        sheetDelivered.show(this.parentFragmentManager, DeliveredBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_AS_SHIPPED -> {
        val sheetShipped = ShippedBottomSheetDialog()
        sheetShipped.setData(orderItem)
        sheetShipped.onClicked = { shippedOrder(it) }
        sheetShipped.show(this.parentFragmentManager, ShippedBottomSheetDialog::class.java.name)
      }
    }
  }

  private fun shippedOrder(markAsShippedRequest: MarkAsShippedRequest) {
    showProgress()
    viewModel?.markAsShipped(clientId, markAsShippedRequest)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        apiGetOrderDetails()
        showLongToast(resources.getString(R.string.order_shipped))
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun deliveredOrder(message: String) {
    showProgress()
    viewModel?.markAsDelivered(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        if (message.isNotEmpty()) {
          updateReason(resources.getString(R.string.order_delivery), UpdateExtraPropertyRequest.PropertyType.DELIVERY.name, ExtraPropertiesOrder(deliveryRemark = message))
        } else {
          apiGetOrderDetails()
          showLongToast(resources.getString(R.string.order_cancel))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiCancelOrder(cancellingEntity: String, reasonText: String) {
    showProgress()
    viewModel?.cancelOrder(clientId, this.orderItem?._id, cancellingEntity)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        val data = it as? OrderConfirmStatus
        if (reasonText.isNotEmpty()) {
          updateReason(resources.getString(R.string.order_cancel), UpdateExtraPropertyRequest.PropertyType.CANCELLATION.name, ExtraPropertiesOrder(cancellationRemark = reasonText))
        } else {
          apiGetOrderDetails()
          showLongToast(resources.getString(R.string.order_cancel))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun updateReason(message: String, type: String, extraPropertiesOrder: ExtraPropertiesOrder) {
    val propertyRequest = UpdateOrderNPropertyRequest(updateExtraPropertyType = type,
        existingKeyName = "", orderId = this.orderItem?._id, extraPropertiesOrder = extraPropertiesOrder)
    viewModel?.updateExtraPropertyOrder(clientId, requestCancel = propertyRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) showLongToast(message)
      apiGetOrderDetails()
    })
  }

  private fun apiConfirmOrder(isSendPaymentLink: Boolean) {
    showProgress()
    viewModel?.confirmOrder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        if (isSendPaymentLink) sendPaymentLinkOrder(getString(R.string.order_confirmed))
        else {
          apiGetOrderDetails()
          showLongToast(getString(R.string.order_confirmed))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendPaymentLinkOrder(message: String) {
    viewModel?.sendPaymentReminder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, { it1 ->
      if (it1.isSuccess()) showLongToast(message)
      apiGetOrderDetails()
    })
  }


  private fun markCodPaymentRequest() {
    showProgress()
    viewModel?.markCodPaymentDone(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        apiGetOrderDetails()
        showLongToast(getString(R.string.order_payment_done))
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiGetOrderDetails() {
    viewModel?.getOrderDetails(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      val response = (it as? OrderDetailResponse)?.Data
      if (it.isSuccess() && response != null) {
        if (position != null && orderList.size > position!!) {
          orderAdapter?.setRefreshItem(position!!, response)
        } else loadNewData()
      } else loadNewData()
    })
  }

  private fun loadNewData() {
    isLoadingD = false
    isLastPageD = false
    orderAdapter?.clear()
    orderListFinalList.clear()
    orderList.clear()
    apiOrderListCall()
  }

  private fun apiOrderListCall() {
    when (OrderSummaryModel.OrderSummaryType.fromType(orderItemType)) {
      OrderSummaryModel.OrderSummaryType.RECEIVED -> {
        val statusList = arrayListOf(OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name)
        requestFilter = getRequestFilterData(statusList)
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      OrderSummaryModel.OrderSummaryType.SUCCESSFUL -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name))
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      OrderSummaryModel.OrderSummaryType.CANCELLED -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name),
            paymentStatus = PaymentDetailsN.STATUS.CANCELLED.name, operatorType = QueryObject.Operator.NE.name)
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      OrderSummaryModel.OrderSummaryType.ABANDONED -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name),
            paymentStatus = PaymentDetailsN.STATUS.CANCELLED.name)
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      OrderSummaryModel.OrderSummaryType.ESCALATED -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ESCALATED.name))
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      else -> {
        requestFilter = getRequestFilterData(arrayListOf())
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
    }

  }

  private fun getSellerOrdersFilterApi(request: OrderFilterRequest, isFirst: Boolean = false, isRefresh: Boolean = false, isSearch: Boolean = false) {
    if (isFirst || isSearch) binding?.progress?.visible()
    viewModel?.getSellerOrdersFilter(auth, request)?.observeOnce(viewLifecycleOwner, Observer {
      binding?.progress?.gone()
      if (it.error is NoNetworkException) {
        errorView(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = (it as? InventoryOrderListResponse)?.Data
        if (isSearch.not()) {
          if (isRefresh) orderListFinalList.clear()
          if (response != null && response.Items.isNullOrEmpty().not()) {
            orderList.clear()
            removeLoader()
            val list = response.Items ?: ArrayList()
            TOTAL_ELEMENTS = response.total()
            orderListFinalList.addAll(list)
            orderList.addAll(orderListFinalList)
            isLastPageD = (orderListFinalList.size == TOTAL_ELEMENTS)
            setAdapterNotify(orderList)
          } else errorView("No order available.")
        } else {
          if (response != null && response.Items.isNullOrEmpty().not()) {
            orderList.clear()
            orderList.addAll(response.Items!!)
            setAdapterNotify(orderList)
          } else if (orderListFinalList.isNullOrEmpty().not()) {
            orderList.clear()
            orderList.addAll(orderListFinalList)
            setAdapterNotify(orderList)
          } else errorView("No order available.")
        }
      } else errorView(it.message ?: "No order available.")
    })
  }

  private fun removeLoader() {
    if (isLoadingD) {
      orderAdapter?.removeLoadingFooter()
      isLoadingD = false
    }
  }

  private fun setAdapterNotify(items: ArrayList<OrderItem>) {
    binding?.orderRecycler?.visible()
    binding?.errorTxt?.gone()
    if (orderAdapter != null) {
      orderAdapter?.notify(getNewList(items))
    } else setAdapterOrderList(getNewList(items))
  }

  private fun getNewList(items: ArrayList<OrderItem>): ArrayList<OrderItem> {
    val list = ArrayList<OrderItem>()
    list.addAll(items)
    return list
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.queryHint = resources.getString(R.string.queryHintOrder)
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toUpperCase(Locale.ROOT)) }
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
    if (query.isNotEmpty() && query.length > 2) {
      isSerchItem = true
      getSellerOrdersFilterApi(getRequestFilterData(arrayListOf(), searchTxt = query), isSearch = isSerchItem)
    } else {
      isSerchItem = false
      orderList.clear()
      orderList.addAll(orderListFinalList)
      setAdapterNotify(orderList)
    }
  }

  private fun errorOnSummary(message: String?) {
    binding?.typeRecycler?.gone()
    binding?.viewShadow?.gone()
    binding?.progress?.gone()
    message?.let { showShortToast(it) }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val isRefresh = bundle?.getBoolean(IntentConstant.IS_REFRESH.name)
      if (isRefresh != null && isRefresh) loadNewData()
    }
  }

  private fun errorView(error: String) {
    binding?.orderRecycler?.gone()
    binding?.errorTxt?.visible()
    binding?.errorTxt?.text = error
  }

  private fun getRequestFilterData(statusList: ArrayList<String>, paymentStatus: String? = null, operatorType: String = QueryObject.Operator.EQ.name, searchTxt: String = ""): OrderFilterRequest {
    val requestFil: OrderFilterRequest?
    if (searchTxt.isEmpty()) {
      currentPage = PAGE_START
      requestFil = OrderFilterRequest(clientId = clientId, skip = currentPage, limit = PAGE_SIZE)
    } else requestFil = OrderFilterRequest(clientId = clientId)
    requestFil.filterBy.add(OrderFilterRequestItem(QueryConditionType = OrderFilterRequestItem.Condition.AND.name, QueryObject = getQueryList(paymentStatus, operatorType)))
    if (statusList.isNullOrEmpty().not()) {
      requestFil.filterBy.add(OrderFilterRequestItem(QueryConditionType = OrderFilterRequestItem.Condition.OR.name, QueryObject = getQueryStatusList(statusList)))
    }
    if (searchTxt.isNotEmpty()) {
      requestFil.filterBy.add(OrderFilterRequestItem(QueryConditionType = OrderFilterRequestItem.Condition.OR.name, QueryObject = getQueryFilter(searchTxt)))
    }
    return requestFil
  }

  private fun getQueryList(paymentStatus: String? = null, operatorType: String): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    queryList.add(QueryObject(QueryObject.QueryKey.Identifier.value, fpTag, QueryObject.Operator.EQ.name))
    queryList.add(QueryObject(QueryObject.QueryKey.Mode.value, OrderSummaryRequest.OrderMode.DELIVERY.name, QueryObject.Operator.EQ.name))
    paymentStatus?.let { queryList.add(QueryObject(QueryObject.QueryKey.PaymentStatus.value, paymentStatus, operatorType)) }
    return queryList
  }

  private fun getQueryFilter(searchTxt: String): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    queryList.add(QueryObject(QueryObject.QueryKey.ReferenceNumber.value, searchTxt, QueryObject.Operator.EQ.name))
    return queryList
  }

  private fun getQueryStatusList(statusList: ArrayList<String>): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    statusList.forEach { queryList.add(QueryObject(QueryObject.QueryKey.Status.value, it, QueryObject.Operator.EQ.name)) }
    return queryList
  }
}
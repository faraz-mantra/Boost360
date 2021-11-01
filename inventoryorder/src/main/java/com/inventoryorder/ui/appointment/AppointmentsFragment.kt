package com.inventoryorder.ui.appointment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.firestore.FirestoreManager
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.*
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentAppointmentsBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.orderRequest.paymentRequest.PaymentReceivedRequest
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequestItem
import com.inventoryorder.model.orderfilter.QueryObject
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.PaginationScrollListener
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.order.InventoryOrderListResponse
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.appointmentSpa.sheetAptSpa.*
import com.inventoryorder.ui.bottomsheet.FilterBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AppointmentsFragment : BaseInventoryFragment<FragmentAppointmentsBinding>(), RecyclerItemClickListener, AppOnZeroCaseClicked {

  private lateinit var zeroCaseFragment: AppFragmentZeroCase
  private lateinit var requestFilter: OrderFilterRequest
  private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
  private var orderList = ArrayList<OrderItem>()
  private var orderListFinalList = ArrayList<OrderItem>()
  private var layoutManager: LinearLayoutManager? = null
  private var filterItem: FilterModel? = null
  private var filterList: ArrayList<FilterModel> = FilterModel().getDataAppointments()
  private var searchView: SearchView? = null
  lateinit var mPopupWindow: PopupWindow
  private var orderItem: OrderItem? = null
  private var position: Int? = null
  private  val TAG = "AppointmentsFragment"
  /* Paging */
  private var isLoadingD = false
  private var isSearchItem = false
  private var TOTAL_ELEMENTS = 0
  private var currentPage = PAGE_START
  private var isLastPageD = false

  var data: PreferenceData? = null

  companion object {
    fun newInstance(bundle: Bundle? = null): AppointmentsFragment {
      val fragment = AppointmentsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(APPOINTMENT_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    data = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as PreferenceData
    setOnClickListener(binding?.btnAdd)
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
    requestFilter = getRequestFilterData(arrayListOf())
    getSellerOrdersFilterApi(requestFilter, isFirst = true)
    binding?.swipeRefresh?.setColorSchemeColors(getColor(R.color.colorAccent))
    binding?.swipeRefresh?.setOnRefreshListener { loadNewData() }
    this.zeroCaseFragment = AppRequestZeroCaseBuilder(AppZeroCases.APPOINTMENT, this, baseActivity).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, zeroCaseFragment,false)

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAdd -> {
        addAppointment()
      }
    }
  }

  private fun addAppointment() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, data)
    bundle.putBoolean(IntentConstant.IS_VIDEO.name, false)
    startFragmentOrderActivity(FragmentType.CREATE_APPOINTMENT_VIEW, bundle, isResult = true)
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.bookingRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && binding?.btnAdd?.visibility == View.VISIBLE) binding?.btnAdd?.hide()
        else if (dy < 0 && binding?.btnAdd?.visibility != View.VISIBLE) binding?.btnAdd?.show()
      }

      override fun loadMoreItems() {
        if (!isLastPageD && !isSearchItem) {
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

  private fun getSellerOrdersFilterApi(
    request: OrderFilterRequest,
    isFirst: Boolean = false,
    isRefresh: Boolean = false,
    isSearch: Boolean = false
  ) {
    if (isFirst || isSearch) showProgressLoad()
    viewModel?.getSellerOrdersFilter(request)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgressLoad()
      if (it.isSuccess()) {
        val response = (it as? InventoryOrderListResponse)?.Data
        if (isSearch.not()) {
          if (isRefresh) orderListFinalList.clear()
          val isDataNotEmpty = (response != null && response.Items.isNullOrEmpty().not())
          onInClinicAptAddedOrUpdated(isDataNotEmpty)//Dr score
          if (isDataNotEmpty) {
            nonEmptyView()
            orderList.clear()
            removeLoader()
            val list = response!!.Items?.map { item ->
              item.recyclerViewType = RecyclerViewItemType.APPOINTMENT_ITEM_TYPE.getLayout();item
            } as ArrayList<OrderItem>
            TOTAL_ELEMENTS = response.total()
            orderListFinalList.addAll(list)
            orderList.addAll(orderListFinalList)
            isLastPageD = (orderList.size == TOTAL_ELEMENTS)
            setAdapterNotify(orderList)
            setToolbarTitle(resources.getString(R.string.appointments) + " ($TOTAL_ELEMENTS)")
          } else emptyView()
        } else {
          if (response != null && response.Items.isNullOrEmpty().not()) {
            nonEmptyView()
            val list = response.Items?.map { item ->
              item.recyclerViewType = RecyclerViewItemType.APPOINTMENT_ITEM_TYPE.getLayout();item
            } as ArrayList<OrderItem>
            orderList.clear()
            orderList.addAll(list)
            setAdapterNotify(orderList)
          } else if (orderList.isNullOrEmpty().not()) {
            orderList.clear()
            orderList.addAll(orderListFinalList)
            setAdapterNotify(orderList)
          } else emptyView()
        }
      } else showLongToast(it.message())
    })
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding?.mainlayout?.visible()
    binding?.childContainer?.gone()
  }

  private fun emptyView() {
    Log.i(TAG, "emptyView: ")
    setHasOptionsMenu(false)
    binding?.mainlayout?.gone()
    binding?.childContainer?.visible()

  }

  private fun showProgressLoad() {
    if (binding?.swipeRefresh?.isRefreshing == false) binding?.progress?.visible()
  }

  private fun hideProgressLoad() {
    binding?.swipeRefresh?.isRefreshing = false
    binding?.progress?.gone()
  }

  private fun onInClinicAptAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    instance.getDrScoreData()?.metricdetail?.boolean_create_sample_in_clinic_appointment = isAdded
    instance.updateDocument()
  }

  private fun removeLoader() {
    if (isLoadingD) {
      orderAdapter?.removeLoadingFooter()
      isLoadingD = false
    }
  }

  private fun setAdapterNotify(items: ArrayList<OrderItem>) {
    binding?.bookingRecycler?.visible()
    nonEmptyView()
    if (orderAdapter != null) {
      orderAdapter?.notify(getDateWiseFilter(items))
    } else setAdapterAppointmentList(getDateWiseFilter(items))
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

  private fun setAdapterAppointmentList(list: ArrayList<OrderItem>) {
    binding?.bookingRecycler?.post {
      orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.bookingRecycler?.layoutManager = layoutManager
      binding?.bookingRecycler?.adapter = orderAdapter
      binding?.bookingRecycler?.let { orderAdapter?.runLayoutAnimation(it) }
    }
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
      FilterModel.FilterType.ALL_APPOINTMENTS -> {
        requestFilter = getRequestFilterData(arrayListOf())
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      FilterModel.FilterType.CONFIRM -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name))
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      FilterModel.FilterType.DELIVERED -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name))
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      FilterModel.FilterType.CANCELLED -> {
        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name))
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
      else -> {
        requestFilter = getRequestFilterData(arrayListOf())
        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      searchView = searchItem.actionView as? SearchView
      val searchEditText: EditText? = searchView?.findViewById(androidx.appcompat.R.id.search_src_text)
      searchEditText?.setTextColor(Color.WHITE)
      searchEditText?.setHintTextColor(getColor(R.color.white_50))
      searchView?.queryHint = resources.getString(R.string.queryHintAppointment)
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
    if (query.isNotEmpty() && query.length > 2) {
      isSearchItem = true
      getSellerOrdersFilterApi(
        getRequestFilterData(
          arrayListOf(),
          searchTxt = query,
          type = QueryObject.QueryKey.ReferenceNumber.name
        ), isSearch = true
      )
    } else {
      isSearchItem = false
      orderList.clear()
      orderList.addAll(orderListFinalList)
      setAdapterNotify(orderList)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal -> {
        val orderItem = item as? OrderItem
        val bundle = Bundle()
        bundle.putString(IntentConstant.ORDER_ID.name, orderItem?._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentOrderActivity(FragmentType.APPOINTMENT_DETAIL_VIEW, bundle, isResult = true)
      }
      RecyclerViewActionType.ORDER_BUTTON_CLICKED.ordinal -> {
        this.position = position
        this.orderItem = (item as? OrderItem)
        this.orderItem?.appointmentButtonStatus()?.firstOrNull()
          ?.let { clickActionAptButton(it, this.orderItem!!) }
      }
      RecyclerViewActionType.ORDER_DROPDOWN_CLICKED.ordinal -> {
        if (::mPopupWindow.isInitialized && mPopupWindow.isShowing) mPopupWindow.dismiss()
        val orderMenu = OrderMenuModel.MenuStatus.from((item as? OrderMenuModel)?.type) ?: return
        this.orderItem?.let { clickActionAptButton(orderMenu, it) }
      }
    }
  }

  override fun onItemClickView(
    position: Int,
    view: View,
    item: BaseRecyclerViewItem?,
    actionType: Int
  ) {
    when (actionType) {
      RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal -> popUpMenuButton(
        position,
        view,
        (item as? OrderItem)
      )
    }
  }

  private fun popUpMenuButton(position: Int, view: View, orderItem: OrderItem?) {
    if (orderItem == null) return
    this.orderItem = orderItem
    this.position = position
    val list = OrderMenuModel().getAppointmentMenu(orderItem)
    if (list.isNotEmpty()) list.removeAt(0)
    val orderMenuView: View =
      LayoutInflater.from(baseActivity).inflate(R.layout.menu_order_button, null)
    val rvOrderMenu: RecyclerView? = orderMenuView.findViewById(R.id.rv_menu_order)
    rvOrderMenu?.apply {
      val adapterMenu = AppBaseRecyclerViewAdapter(baseActivity, list, this@AppointmentsFragment)
      adapter = adapterMenu
    }
    mPopupWindow = PopupWindow(
      orderMenuView,
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      true
    )
    if (orderAdapter != null && orderList.size > 2 && orderAdapter!!.list().size - 1 == position) {
      orderMenuView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
      )
      mPopupWindow.showAsDropDown(
        view,
        view.x.roundToInt(),
        view.y.roundToInt() - orderMenuView.measuredHeight,
        Gravity.NO_GRAVITY
      )
    } else mPopupWindow.showAsDropDown(view, 0, 0)
  }

  private fun clickActionAptButton(orderMenu: OrderMenuModel.MenuStatus, orderItem: OrderItem) {
    when (orderMenu) {
      OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
        val sheetConfirm = ConfirmAptSheetDialog()
        sheetConfirm.setData(orderItem)
        sheetConfirm.onClicked = { apiConfirmApt(it) }
        sheetConfirm.show(this.parentFragmentManager, ConfirmAptSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
        val sheetRequestPayment = RequestPaymentAptSheetDialog()
        sheetRequestPayment.setData(orderItem)
        sheetRequestPayment.onClicked = {
          showProgress()
          sendPaymentLinkApt(getString(R.string.payment_request_send))
        }
        sheetRequestPayment.show(
          this.parentFragmentManager,
          RequestPaymentAptSheetDialog::class.java.name
        )
      }
      OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
        this.orderItem = orderItem
        val sheetCancel = CancelAptSheetDialog()
        sheetCancel.setData(orderItem)
        sheetCancel.onClicked = this@AppointmentsFragment::apiCancelApt
        sheetCancel.show(this.parentFragmentManager, CancelAptSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> {
        val markPaymentDoneSheet = MarkPaymentDoneAptSheetDialog()
        markPaymentDoneSheet.setData(orderItem)
        markPaymentDoneSheet.onClicked = { markReceivedPaymentRequest(it) }
        markPaymentDoneSheet.show(
          this.parentFragmentManager,
          MarkPaymentDoneAptSheetDialog::class.java.name
        )
      }
      OrderMenuModel.MenuStatus.MARK_AS_SERVED -> {
        val sheetServed = ServedAptSheetDialog()
        sheetServed.setData(orderItem)
        sheetServed.onClicked = { serveCustomer("") }
        sheetServed.show(this.parentFragmentManager, ServedAptSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.START_APPOINTMENT -> {
        val sheetStartApt = StartAptSheetDialog()
        sheetStartApt.setData(orderItem)
        sheetStartApt.onClicked = { startApt(it) }
        sheetStartApt.show(this.parentFragmentManager, StartAptSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_FEEDBACK -> {
        val sheetFeedbackApt = SendFeedbackAptSheetDialog()
        sheetFeedbackApt.setData(orderItem)
        sheetFeedbackApt.onClicked = { sendFeedbackRequestApt(it) }
        sheetFeedbackApt.show(
          this.parentFragmentManager,
          SendFeedbackAptSheetDialog::class.java.name
        )
      }
      OrderMenuModel.MenuStatus.SEND_RE_BOOKING -> {
        val sheetReBookingApt = SendReBookingAptSheetDialog()
        sheetReBookingApt.setData(orderItem)
        sheetReBookingApt.onClicked = { sendReBookingRequestApt() }
        sheetReBookingApt.show(
          this.parentFragmentManager,
          SendReBookingAptSheetDialog::class.java.name
        )
      }
      else -> {
      }
    }
  }

  private fun startApt(markAsShippedRequest: MarkAsShippedRequest) {
    showProgress()
    viewModel?.markAsShipped(clientId, markAsShippedRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        apiGetAptDetails()
        showLongToast(resources.getString(R.string.apt_start_success))
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendReBookingRequestApt() {
    showProgress()
    viewModel?.sendReBookingReminder(clientId, this.orderItem?._id)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          apiGetAptDetails()
          showLongToast(resources.getString(R.string.re_booking_reminder))
        } else {
          showLongToast(it.message())
          hideProgress()
        }
      })
  }

  private fun sendFeedbackRequestApt(request: FeedbackRequest) {
    showProgress()
    viewModel?.sendOrderFeedbackRequest(clientId, request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        apiGetAptDetails()
        showLongToast(resources.getString(R.string.appointment_feedback_requested))
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun serveCustomer(message: String) {
    showProgress()
    viewModel?.markAsDelivered(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (message.isNotEmpty()) {
          updateReason(
            resources.getString(R.string.appointment_serve),
            UpdateExtraPropertyRequest.PropertyType.DELIVERY.name,
            ExtraPropertiesOrder(deliveryRemark = message)
          )
        } else {
          apiGetAptDetails()
          showLongToast(resources.getString(R.string.appointment_serve))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiCancelApt(cancellingEntity: String, reasonText: String) {
    showProgress()
    viewModel?.cancelOrder(clientId, this.orderItem?._id, cancellingEntity)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          val data = it as? OrderConfirmStatus
          if (reasonText.isNotEmpty()) {
            updateReason(
              resources.getString(R.string.appointment_cancel),
              UpdateExtraPropertyRequest.PropertyType.CANCELLATION.name,
              ExtraPropertiesOrder(cancellationRemark = reasonText)
            )
          } else {
            apiGetAptDetails()
            showLongToast(resources.getString(R.string.appointment_cancel))
          }
        } else {
          showLongToast(it.message())
          hideProgress()
        }
      })
  }

  private fun updateReason(
    message: String,
    type: String,
    extraPropertiesOrder: ExtraPropertiesOrder
  ) {
    val propertyRequest = UpdateOrderNPropertyRequest(
      updateExtraPropertyType = type,
      existingKeyName = "",
      orderId = this.orderItem?._id,
      extraPropertiesOrder = extraPropertiesOrder
    )
    viewModel?.updateExtraPropertyOrder(clientId, requestCancel = propertyRequest)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) showLongToast(message)
        apiGetAptDetails()
      })
  }

  private fun apiConfirmApt(isSendPaymentLink: Boolean) {
    showProgress()
    viewModel?.confirmOrder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (isSendPaymentLink) sendPaymentLinkApt(getString(R.string.appointment_confirmed))
        else {
          apiGetAptDetails()
          showLongToast(getString(R.string.appointment_confirmed))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendPaymentLinkApt(message: String) {
    viewModel?.sendPaymentReminder(clientId, this.orderItem?._id)
      ?.observeOnce(viewLifecycleOwner, { it1 ->
        if (it1.isSuccess()) showLongToast(message)
        apiGetAptDetails()
      })
  }

  private fun markReceivedPaymentRequest(request: PaymentReceivedRequest) {
    showProgress()
    viewModel?.markPaymentReceivedMerchant(clientId, request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        apiGetAptDetails()
        showLongToast(getString(R.string.payment_confirmed))
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiGetAptDetails() {
    viewModel?.getOrderDetails(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      val response = (it as? OrderDetailResponse)?.Data
      if (it.isSuccess() && response != null) {
        if (position != null && orderAdapter != null && orderAdapter!!.list().size > position!!) {
          orderListFinalList =
            orderListFinalList.map { item -> if (item._id.equals(response._id)) response else item } as ArrayList<OrderItem>
          response.recyclerViewType = RecyclerViewItemType.APPOINTMENT_ITEM_TYPE.getLayout()
          orderAdapter?.setRefreshItem(position!!, response)
        } else loadNewData()
      } else loadNewData()
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val isRefresh = bundle?.getBoolean(IntentConstant.IS_REFRESH.name)
      if (isRefresh != null && isRefresh) clickFilterItem(filterItem)
    }
  }

  private fun getRequestFilterData(
    statusList: ArrayList<String>,
    searchTxt: String = "",
    type: String = QueryObject.QueryKey.ReferenceNumber.name
  ): OrderFilterRequest {
    val requestFil: OrderFilterRequest?
    if (searchTxt.isEmpty()) {
      currentPage = PAGE_START
      requestFil = OrderFilterRequest(clientId = clientId, skip = currentPage, limit = PAGE_SIZE)
    } else requestFil = OrderFilterRequest(clientId = clientId)
    requestFil.filterBy.add(
      OrderFilterRequestItem(
        QueryConditionType = OrderFilterRequestItem.Condition.AND.name,
        QueryObject = getQueryList()
      )
    )
    if (statusList.isNullOrEmpty().not()) {
      requestFil.filterBy.add(
        OrderFilterRequestItem(
          QueryConditionType = OrderFilterRequestItem.Condition.OR.name,
          QueryObject = getQueryStatusList(statusList)
        )
      )
    }
    if (searchTxt.isNotEmpty()) {
      requestFil.filterBy.add(
        OrderFilterRequestItem(
          QueryConditionType = OrderFilterRequestItem.Condition.OR.name,
          QueryObject = getQueryFilter(type, searchTxt)
        )
      )
    }
    return requestFil
  }

  private fun getQueryList(): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    queryList.add(
      QueryObject(
        QueryObject.QueryKey.Identifier.value,
        fpTag,
        QueryObject.Operator.EQ.name
      )
    )
    queryList.add(
      QueryObject(
        QueryObject.QueryKey.Mode.value,
        OrderSummaryRequest.OrderMode.APPOINTMENT.name,
        QueryObject.Operator.EQ.name
      )
    )
    queryList.add(
      QueryObject(
        QueryObject.QueryKey.DeliveryMode.value,
        OrderSummaryRequest.DeliveryMode.ONLINE.name,
        QueryObject.Operator.NE.name
      )
    )
    queryList.add(
      QueryObject(
        QueryObject.QueryKey.DeliveryProvider.value,
        QueryObject.QueryValue.NF_VIDEO_CONSULATION.name,
        QueryObject.Operator.NE.name
      )
    )
    return queryList
  }

  private fun getQueryFilter(type: String, searchTxt: String): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    val searchType = when (type) {
      QueryObject.QueryKey.BuyerFullName.name -> QueryObject.QueryKey.BuyerFullName.value
      QueryObject.QueryKey.BuyerPrimaryContactNumber.name -> QueryObject.QueryKey.BuyerPrimaryContactNumber.value
      else -> QueryObject.QueryKey.ReferenceNumber.value
    }
    queryList.add(QueryObject(searchType, searchTxt, QueryObject.Operator.EQ.name))
    return queryList
  }

  private fun getQueryStatusList(statusList: ArrayList<String>): ArrayList<QueryObject> {
    val queryList = ArrayList<QueryObject>()
    statusList.forEach {
      queryList.add(
        QueryObject(
          QueryObject.QueryKey.Status.value,
          it,
          QueryObject.Operator.EQ.name
        )
      )
    }
    return queryList
  }

  private fun loadNewData() {
    isLoadingD = false
    isLastPageD = false
    orderAdapter?.clear()
    orderListFinalList.clear()
    orderList.clear()
    clickFilterItem(filterItem)
  }

  override fun primaryButtonClicked() {
    addAppointment()
  }

  override fun secondaryButtonClicked() {
    showShortToast(getString(R.string.coming_soon))
    //TODO add tutorial video flow
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }



//  private fun apiOrderListCall() {
//    when (OrderSummaryModel.OrderSummaryType.fromType(orderItemType)) {
//      OrderSummaryModel.OrderSummaryType.RECEIVED -> {
//        val statusList = arrayListOf(OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name)
//        requestFilter = getRequestFilterData(statusList)
//        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//      }
//      OrderSummaryModel.OrderSummaryType.SUCCESSFUL -> {
//        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name))
//        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//      }
//      /*  OrderSummaryModel.OrderSummaryType.CANCELLED -> {
//          requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name),
//                  paymentStatus = PaymentDetailsN.STATUS.CANCELLED.name, operatorType = QueryObject.Operator.NE.name)
//          getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//        }
//        OrderSummaryModel.OrderSummaryType.ABANDONED -> {
//          requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name),
//                  paymentStatus = PaymentDetailsN.STATUS.CANCELLED.name)
//          getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//        }*/
//      OrderSummaryModel.OrderSummaryType.ESCALATED -> {
//        requestFilter = getRequestFilterData(arrayListOf(OrderSummaryModel.OrderStatus.ESCALATED.name))
//        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//      }
//      else -> {
//        requestFilter = getRequestFilterData(arrayListOf())
//        getSellerOrdersFilterApi(requestFilter, isFirst = true, isRefresh = true)
//      }
//    }
//
//  }
}
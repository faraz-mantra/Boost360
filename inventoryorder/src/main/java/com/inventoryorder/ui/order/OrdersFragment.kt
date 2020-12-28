package com.inventoryorder.ui.order

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
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
import com.inventoryorder.databinding.FragmentOrdersBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequestItem
import com.inventoryorder.model.orderfilter.QueryObject
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
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
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import com.inventoryorder.utils.WebEngageController
import java.util.*
import kotlin.collections.ArrayList

class OrdersFragment : BaseInventoryFragment<FragmentOrdersBinding>(), RecyclerItemClickListener {

    private lateinit var requestFilter: OrderFilterRequest
    private var typeAdapter: AppBaseRecyclerViewAdapter<OrderSummaryModel>? = null
    private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
    private var typeList: ArrayList<OrderSummaryModel>? = null
    private var orderList = ArrayList<OrderItem>()
    private var orderListFilter = ArrayList<OrderItem>()
    private var layoutManager: LinearLayoutManager? = null

    /* Paging */
    private var isLoadingD = false
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
        setOnClickListener(binding?.btnAdd)
        apiSellerSummary()
        layoutManager = LinearLayoutManager(baseActivity)
        layoutManager?.let { scrollPagingListener(it) }
        setOnClickListener(binding?.btnAdd)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnAdd -> {
                showLongToast("Coming soon...")
//        startFragmentActivity(FragmentType.CREATE_NEW_BOOKING, Bundle())
            }
        }
    }

    private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
        binding?.orderRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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

    private fun apiSellerSummary() {
        binding?.progress?.visible()
        viewModel?.getSellerSummary(clientId, fpTag)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.error is NoNetworkException) {
                errorOnSummary(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                val response = it as? OrderSummaryResponse
                typeList = response?.Data?.getOrderType()
                typeList?.let { it1 -> setAdapterSellerSummary(it1) } ?: errorOnSummary(null)
            } else errorOnSummary(it?.message)
        })
    }

    private fun setAdapterOrderList(list: ArrayList<OrderItem>) {
        binding?.orderRecycler?.post {
            orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
            binding?.orderRecycler?.layoutManager = layoutManager
            binding?.orderRecycler?.adapter = orderAdapter
            binding?.orderRecycler?.let { orderAdapter?.runLayoutAnimation(it) }
        }
    }

    private fun setAdapterSellerSummary(typeList: ArrayList<OrderSummaryModel>) {
        binding?.typeRecycler?.visible()
        binding?.viewShadow?.visible()
        orderList.clear()
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
                startFragmentActivity(FragmentType.ORDER_DETAIL_VIEW, bundle, isResult = true)
            }
            RecyclerViewActionType.ORDER_SUMMARY_CLICKED.ordinal -> {
                val orderItem = item as? OrderSummaryModel
                typeList?.forEach { it.isSelected = (it.type == orderItem?.type) }
                typeAdapter?.notifyDataSetChanged()
                orderItemType = orderItem?.type ?: OrderSummaryModel.OrderSummaryType.TOTAL.type
                loadNewData()
            }
            RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal -> apiConfirmOrder(position, (item as? OrderItem))
        }
    }

    private fun apiConfirmOrder(position: Int, order: OrderItem?) {
        showProgress()
        viewModel?.confirmOrder(clientId, order?._id)?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()
            if (it.error is NoNetworkException) {
                showShortToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                val data = it as? OrderConfirmStatus
                showLongToast(getString(R.string.order_confirmed))
                val itemList = orderAdapter?.list() as ArrayList<OrderItem>
                if (itemList.size > position) {
                    itemList[position].Status = OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name
                    orderAdapter?.notifyItemChanged(position)
                } else showLongToast(it.message())
            }
        })
    }


    private fun loadNewData() {
        isLoadingD = false
        isLastPageD = false
        orderAdapter?.clear()
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
                    if (isRefresh) orderList.clear()
                    if (response != null && response.Items.isNullOrEmpty().not()) {
                        removeLoader()
                        val list = response.Items ?: ArrayList()
                        TOTAL_ELEMENTS = response.total()
                        orderList.addAll(list)
                        isLastPageD = (orderList.size == TOTAL_ELEMENTS)
                        setAdapterNotify(orderList)
                    } else errorView("No order available.")
                } else {
                    if (response != null && response.Items.isNullOrEmpty().not()) {
                        setAdapterNotify(response.Items!!)
                    } else if (orderList.isNullOrEmpty().not()) setAdapterNotify(orderList)
                    else errorView("No order available.")
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
            getSellerOrdersFilterApi(getRequestFilterData(arrayListOf(), searchTxt = query), isSearch = true)
        } else setAdapterNotify(orderList)
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
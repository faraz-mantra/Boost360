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
import com.framework.base.BaseResponse
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

class OrdersFragment : BaseInventoryFragment<FragmentOrdersBinding>(), RecyclerItemClickListener {

  private var request: OrderSummaryRequest? = null
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
  private var orderItemType = OrderSummaryModel.OrderType.TOTAL.type

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
    setOnClickListener(binding?.btnAdd)
    apiSellerSummary()
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
    setOnClickListener(binding?.btnAdd)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAdd -> startFragmentActivity(FragmentType.CREATE_NEW_BOOKING, Bundle())
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
          currentPage += request?.limit ?: 0
          orderAdapter?.addLoadingFooter(OrderItem().getLoaderItem())
          request?.skip = currentPage
          apiOrderListCall()
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
    apiOrderList(getRequestData(), true)
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
        orderItemType = orderItem?.type ?: OrderSummaryModel.OrderType.TOTAL.type
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
        data?.let { d -> showLongToast(d.Message as String?) }
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
    currentPage = PAGE_START
    orderAdapter?.clear()
    request?.orderStatus = OrderSummaryModel.OrderType.fromType(orderItemType)?.value
    request?.paymentStatus = null
    request?.skip = currentPage
    orderList.clear()
    binding?.progress?.visible()
    apiOrderListCall()
  }

  private fun apiOrderListCall() {
    request?.let {
      when (OrderSummaryModel.OrderType.fromType(orderItemType)) {
        OrderSummaryModel.OrderType.RECEIVED -> apiAssureOrder(it)
//        OrderSummaryModel.OrderType.SUCCESSFUL -> apiInCompleteOrder(it)
        OrderSummaryModel.OrderType.CANCELLED -> apiCancelOrder(it)
        OrderSummaryModel.OrderType.ABANDONED -> {
          request?.paymentStatus = PaymentDetailsN.STATUS.CANCELLED.name
          apiOrderList(it)
        }
        else -> apiOrderList(it)
      }
    }

  }

  private fun apiOrderList(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getSellerOrders(auth, request)?.observeOnce(viewLifecycleOwner, Observer { res -> res?.let { responseOrderList(isFirst, it) } })
  }

  private fun apiAssureOrder(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getAssurePurchaseOrder(request)?.observeOnce(viewLifecycleOwner, Observer { res -> res?.let { responseOrderList(isFirst, it) } })
  }

  private fun apiInCompleteOrder(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getInCompleteOrders(request)?.observeOnce(viewLifecycleOwner, Observer { res -> res?.let { responseOrderList(isFirst, it) } })
  }

  private fun apiCancelOrder(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getCancelledOrders(request)?.observeOnce(viewLifecycleOwner, Observer { res -> res?.let { responseOrderList(isFirst, it) } })
  }

  private fun responseOrderList(isFirst: Boolean, it: BaseResponse) {
    binding?.progress?.gone()
    if (it.error is NoNetworkException) {
      showShortToast(resources.getString(R.string.internet_connection_not_available))
    } else {
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = (it as? InventoryOrderListResponse)?.Data ?: return
        binding?.orderRecycler?.visible()
        val list = response.Items ?: ArrayList()
        TOTAL_ELEMENTS = response.total()
        orderList.addAll(list)
        isLastPageD = (orderList.size == TOTAL_ELEMENTS)
        if (isFirst.not() && orderAdapter != null) {
          orderAdapter?.removeLoadingFooter()
          isLoadingD = false
          orderAdapter?.addItems(list)
        } else setAdapterOrderList(list)
      } else {
        binding?.orderRecycler?.gone()
        showShortToast(it.message)
      }
    }
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
          newText?.let { startFilter(it.trim().toLowerCase()) }
          return false
        }
      })
    }
  }

  private fun startFilter(query: String) {
    if (query.isNullOrEmpty().not()) {
      orderListFilter.clear()
      orderListFilter.addAll(orderList)
      orderListFilter.let { it1 ->
        val list = it1.filter {
          it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query) || it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query)
        } as ArrayList<OrderItem>
        orderAdapter?.notify(list)
      }
    } else orderAdapter?.notify(orderList)
  }

  private fun getRequestData(): OrderSummaryRequest {
    request = OrderSummaryRequest(clientId, fpTag, skip = currentPage, limit = PAGE_SIZE)
    return request!!
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
}

//private var pickInventoryNatureBottomSheetDialog : PickInventoryNatureBottomSheetDialog ? = null
//private var selectPickInventoryNatureList = PickInventoryNatureModel().getData()

//private fun showBottomSheetDialogPickInventoryNature(){
//  pickInventoryNatureBottomSheetDialog = PickInventoryNatureBottomSheetDialog()
//  pickInventoryNatureBottomSheetDialog?.onDoneClicked = { selectPickInventoryNatureList (it)}
//  pickInventoryNatureBottomSheetDialog?.setList( selectPickInventoryNatureList )
//  pickInventoryNatureBottomSheetDialog?.show(this.parentFragmentManager, PickInventoryNatureBottomSheetDialog::class.java.name)
//
//}
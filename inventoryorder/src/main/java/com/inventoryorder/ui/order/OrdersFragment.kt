package com.inventoryorder.ui.order

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
import com.inventoryorder.databinding.FragmentInventoryAllOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.PaginationScrollListener
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.inventoryorder.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.SellerSummaryResponse
import com.inventoryorder.rest.response.order.SellerOrderListResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class OrdersFragment : BaseInventoryFragment<FragmentInventoryAllOrderBinding>(), RecyclerItemClickListener {

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
    apiSellerSummary()
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
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
          request?.let { apiSellerOrderList(it) }
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
        val response = it as? SellerSummaryResponse
        typeList = response?.Data?.getOrderType()
        typeList?.let { it1 -> setAdapterSellerSummary(it1) } ?: errorOnSummary(null)
      } else errorOnSummary(it?.message)
    })
  }

  private fun apiSellerOrderList(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getSellerAllOrder(auth, request)?.observeOnce(viewLifecycleOwner, Observer {
      binding?.progress?.gone()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = (it as? SellerOrderListResponse)?.Data ?: return@Observer
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
    apiSellerOrderList(getRequestData(), true)
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
        bundle.putSerializable(IntentConstant.ORDER_ITEM.name, orderItem)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentActivity(FragmentType.ORDER_DETAIL_VIEW, bundle)
      }
      RecyclerViewActionType.ORDER_SUMMARY_CLICKED.ordinal -> {
        val orderItem = item as? OrderSummaryModel
        typeList?.forEach { it.isSelected = (it.type == orderItem?.type) }
        typeAdapter?.notifyDataSetChanged()
        orderItem?.type?.let { loadNewData(it) }

      }
    }
  }

  private fun loadNewData(type: String) {
    isLoadingD = false
    isLastPageD = false
    currentPage = PAGE_START
    orderAdapter?.clear()
    request?.orderStatus = OrderSummaryModel.OrderType.fromType(type)?.value
    request?.skip = currentPage
    orderList.clear()
    binding?.progress?.visible()
    apiSellerOrderList(request!!)
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
}
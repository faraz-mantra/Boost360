package com.inventoryorder.ui.booking

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
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.FragmentInventoryAllBookingsBinding
import com.inventoryorder.model.OrderConfirmStatus
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
import com.inventoryorder.ui.startFragmentActivity
import java.util.*
import kotlin.collections.ArrayList

class BookingsFragment : BaseInventoryFragment<FragmentInventoryAllBookingsBinding>(), RecyclerItemClickListener {

  private var request: OrderSummaryRequest? = null
  private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
  private var orderList = ArrayList<OrderItem>()
  private var orderListFilter = ArrayList<OrderItem>()
  private var layoutManager: LinearLayoutManager? = null

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var currentPage = PAGE_START
  private var isLastPageD = false

  companion object {
    fun newInstance(bundle: Bundle? = null): BookingsFragment {
      val fragment = BookingsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()

    setOnClickListener(binding?.btnAdd)
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
    apiSellerOrderList(getRequestData(), true)
  }

  private fun apiSellerOrderList(request: OrderSummaryRequest, isFirst: Boolean = false) {
    viewModel?.getSellerAllOrder(auth, request)?.observeOnce(viewLifecycleOwner, Observer {
      binding?.progress?.gone()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = (it as? InventoryOrderListResponse)?.Data ?: return@Observer
        binding?.bookingRecycler?.visible()
        val list = (response.Items ?: ArrayList()).map { item ->
          item.recyclerViewType = RecyclerViewItemType.BOOKINGS_ITEM_TYPE.getLayout();item
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

    when(v){
      binding?.btnAdd ->{
        startFragmentActivity(FragmentType.CREATE_NEW_BOOKING,Bundle())
      }
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

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.queryHint = resources.getString(R.string.queryHintBooking)
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
        orderAdapter?.notify(getDateWiseFilter(list))
      }
    } else orderAdapter?.notify(getDateWiseFilter(orderList))
  }

  private fun getRequestData(): OrderSummaryRequest {
    request = OrderSummaryRequest(clientId, fpTag, skip = currentPage, limit = PAGE_SIZE)
    return request!!
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal -> {
        val orderItem = item as? OrderItem
        val bundle = Bundle()
        bundle.putString(IntentConstant.ORDER_ID.name, orderItem?._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentActivity(FragmentType.BOOKING_DETAIL_VIEW, bundle)
      }
      RecyclerViewActionType.BOOKING_CONFIRM_CLICKED.ordinal -> apiConfirmOrder(position, (item as? OrderItem))
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
          itemList[position].Status = OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name
          orderAdapter?.notifyItemChanged(position)
        } else showLongToast(it.message())
      }
    })
  }
}
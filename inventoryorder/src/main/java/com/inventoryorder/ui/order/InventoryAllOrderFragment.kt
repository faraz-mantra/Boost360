package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.SellerSummaryResponse
import com.inventoryorder.rest.response.order.SellerOrderListResponse
import com.inventoryorder.ui.startFragmentActivity

class InventoryAllOrderFragment : BaseOrderFragment<FragmentInventoryAllOrderBinding>(), RecyclerItemClickListener {

  private var previousScrollY: Int = 0
  private var request: OrderSummaryRequest? = null
  private var typeAdapter: AppBaseRecyclerViewAdapter<OrderSummaryModel>? = null
  private var orderAdapter: AppBaseRecyclerViewAdapter<OrderItem>? = null
  private var typeList: ArrayList<OrderSummaryModel>? = null
  private var orderList: ArrayList<OrderItem>? = null
  private var orderListFilter = ArrayList<OrderItem>()

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InventoryAllOrderFragment {
      val fragment = InventoryAllOrderFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    scrollBehaviourSet()
    apiSellerSummary()
  }

  private fun apiSellerSummary() {
    binding?.progress?.visible()
    viewModel?.getSellerSummary(fpTag)?.observeOnce(viewLifecycleOwner, Observer {
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
    if (isFirst.not()) binding?.progress?.visible()
    viewModel?.getSellerAllOrder(auth, request)?.observeOnce(viewLifecycleOwner, Observer {
      binding?.progress?.gone()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it as? SellerOrderListResponse
        orderList = response?.Data?.Items ?: ArrayList()
        orderList?.let { it1 ->
          binding?.orderRecycler?.visible()
          if (isFirst.not() && orderAdapter != null)
            orderAdapter?.notify(it1)
          else setAdapterOrderList(it1)
        }
      } else {
        binding?.orderRecycler?.gone()
        showShortToast(it.message)
      }
    })
  }

  private fun setAdapterOrderList(orderList: ArrayList<OrderItem>) {
    binding?.orderRecycler?.post {
      orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, orderList, this)
      binding?.orderRecycler?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.orderRecycler?.adapter = orderAdapter
      binding?.orderRecycler?.let { orderAdapter?.runLayoutAnimation(it) }
    }
  }

  private fun setAdapterSellerSummary(typeList: ArrayList<OrderSummaryModel>) {
    binding?.typeRecycler?.visible()
    binding?.viewShadow?.visible()
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
        orderItem?.type?.let {
          request?.orderStatus = OrderSummaryModel.OrderType.fromType(it).value
          apiSellerOrderList(request!!)
        }
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.queryHint = resources.getString(R.string.queryHint)
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
      orderList?.let { orderListFilter.addAll(it) }
      orderListFilter.let { it1 ->
        val list = it1.filter {
          it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query) || it.referenceNumber().startsWith(query) || it.referenceNumber().contains(query)
        } as ArrayList<OrderItem>
        orderAdapter?.notify(list)
      }
    } else orderAdapter?.notify(orderList)
  }

  private fun getRequestData(): OrderSummaryRequest {
    request = OrderSummaryRequest(fpTag, skip = 0, limit = 100)
    return request!!
  }

  private fun scrollBehaviourSet() {
    binding?.nestedScroll?.viewTreeObserver?.addOnScrollChangedListener {
      binding?.nestedScroll?.scrollY?.let {
        if (it > previousScrollY && binding?.btnAdd?.visibility === View.VISIBLE) binding?.btnAdd?.hide()
        else if (it < previousScrollY && binding?.btnAdd?.visibility !== View.VISIBLE) binding?.btnAdd?.show()
        previousScrollY = it
      }
    }
  }

  private fun errorOnSummary(message: String?) {
    binding?.typeRecycler?.gone()
    binding?.viewShadow?.gone()
    binding?.progress?.gone()
    message?.let { showShortToast(it) }
  }
}
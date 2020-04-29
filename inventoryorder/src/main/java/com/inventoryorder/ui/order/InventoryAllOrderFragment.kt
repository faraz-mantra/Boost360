package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
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
    apiSellerOrderList(getRequestData())
  }

  private fun apiSellerSummary() {
    viewModel?.getSellerSummary(fpId)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it as? SellerSummaryResponse
        response?.Data?.getOrderType()?.let { it1 -> setAdapterSellerSummary(it1) }
      } else showShortToast(it.message)
    })
  }

  private fun apiSellerOrderList(request: OrderSummaryRequest) {
    viewModel?.getSellerAllOrder(request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it as? SellerOrderListResponse
        response?.Data?.Items?.let { it1 -> setAdapterOrderList(it1) }
      } else showShortToast(it.message)
    })
  }

  private fun setAdapterOrderList(orderList: ArrayList<OrderItem>) {
    binding?.orderRecycler?.post {
      val orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, orderList, this)
      binding?.orderRecycler?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.orderRecycler?.adapter = orderAdapter
      binding?.orderRecycler?.let { orderAdapter.runLayoutAnimation(it) }
    }
  }

  private fun setAdapterSellerSummary(typeList: ArrayList<OrderSummaryModel>) {
    binding?.typeRecycler?.post {
      val typeAdapter = AppBaseRecyclerViewAdapter(baseActivity, typeList, this)
      binding?.typeRecycler?.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.typeRecycler?.adapter = typeAdapter
      binding?.typeRecycler?.let { typeAdapter.runLayoutAnimation(it) }
    }

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal -> {
        startFragmentActivity(FragmentType.ORDER_DETAIL_VIEW, Bundle())
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item = menu.findItem(R.id.menu_item_search)
  }

  private fun getRequestData(): OrderSummaryRequest {
    request = OrderSummaryRequest(fpId, skip = 0, limit = 20)
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
}
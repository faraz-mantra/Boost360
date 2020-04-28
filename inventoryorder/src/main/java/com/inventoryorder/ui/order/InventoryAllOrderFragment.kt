package com.inventoryorder.ui.order

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentInventoryAllOrderBinding
import com.inventoryorder.model.InventoryOrderModel
import com.inventoryorder.model.OrderTypeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class InventoryAllOrderFragment : BaseOrderFragment<FragmentInventoryAllOrderBinding>(), RecyclerItemClickListener {

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
    setAdapterOrder()
  }

  private fun setAdapterOrder() {
    val typeList = OrderTypeModel().getOrderType()
    val orderList = InventoryOrderModel().getOrderItem()
    binding?.typeRecycler?.post {
      val typeAdapter = AppBaseRecyclerViewAdapter(baseActivity, typeList, this)
      binding?.typeRecycler?.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.typeRecycler?.adapter = typeAdapter
      binding?.typeRecycler?.let { typeAdapter.runLayoutAnimation(it) }
    }
    binding?.orderRecycler?.post {
      val orderAdapter = AppBaseRecyclerViewAdapter(baseActivity, orderList, this)
      binding?.orderRecycler?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.orderRecycler?.adapter = orderAdapter
      binding?.orderRecycler?.let { orderAdapter.runLayoutAnimation(it) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal -> {

      }
    }
  }
}
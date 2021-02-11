package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.exceptions.NoNetworkException
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentAddProductBinding
import com.inventoryorder.model.PRODUCT_LIST_CLIENT_ID
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController

class AddProductFragment : BaseInventoryFragment<FragmentAddProductBinding>(), RecyclerItemClickListener {

  private var productList : ArrayList<ProductItem> ?= null
  private var itemsAdapter: AppBaseRecyclerViewAdapter<ProductItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddProductFragment {
      val fragment = AddProductFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent("Clicked on Add Product", "ORDERS", it) }

    layoutManagerN = LinearLayoutManager(baseActivity)

    setOnClickListener(binding?.tvProceed)
    getItemList(fpTag, PRODUCT_LIST_CLIENT_ID)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
        binding?.tvProceed -> {
            startFragmentOrderActivity(FragmentType.BILLING_DETAIL, Bundle())
        }
    }
  }

  private fun getItemList(fpTag : String?, clientId : String?) {
    viewModel?.getProductItems(fpTag, clientId, 0)?.observe(viewLifecycleOwner, Observer {

      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }

      if (it.isSuccess()) {
        val resp = (it.arrayResponse as? Array<ProductItem>)
        productList = if (resp.isNullOrEmpty().not()) resp?.toCollection(ArrayList()) else ArrayList()
        setAdapterOrderList(productList!!)
      } else {
        showShortToast(it.message)
      }
    })
  }

  private fun setAdapterOrderList(list: ArrayList<ProductItem>) {
    binding?.productRecycler?.apply {
      itemsAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this@AddProductFragment)
      layoutManager = layoutManagerN
      adapter = itemsAdapter
      itemsAdapter?.runLayoutAnimation(this)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}
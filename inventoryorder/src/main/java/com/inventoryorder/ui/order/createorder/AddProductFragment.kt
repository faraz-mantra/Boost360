package com.inventoryorder.ui.order.createorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentAddProductBinding
import com.inventoryorder.model.PRODUCT_LIST_CLIENT_ID
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.ProductDetails
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController

class AddProductFragment : BaseInventoryFragment<FragmentAddProductBinding>(), RecyclerItemClickListener {

  private var productList : ArrayList<ProductItem> ?= null
  private var selectedProductList : ArrayList<ProductItem> ?= null
  private var itemsAdapter: AppBaseRecyclerViewAdapter<ProductItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var totalPrice = 0.0
  private var totalCartItems = 0
  private var createOrderRequest = OrderInitiateRequest()

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

    productList = ArrayList()
    selectedProductList = ArrayList()

    getItemList(fpTag, PRODUCT_LIST_CLIENT_ID)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
        binding?.tvProceed -> {
           onProceedClicked()
        }
    }
  }

  private fun onProceedClicked() {

    var itemList = ArrayList<ItemsItem>()

    for (prod in selectedProductList!!) {

      var productDetails = ProductDetails()
      productDetails.currencyCode = prod.CurrencyCode
      productDetails.description = prod.Description
      productDetails.discountAmount = prod.DiscountAmount
      productDetails.imageUri = prod.ImageUri
      productDetails.isAvailable = prod.IsAvailable
      productDetails.name = prod.Name
      productDetails.price = prod.Price
      productDetails.isAvailable = prod.IsAvailable

      var item = ItemsItem(productDetails = productDetails, productOrOfferId = prod._id, )
      item.quantity = prod.productQuantityAdded

      //hardcoded as "prod.productType!!" is throwing error when calling api
      item.type = "PRODUCT"
      //item.type = prod.productType!!
      itemList.add(item)
    }

    createOrderRequest.items = itemList

    var bundle = Bundle()
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    bundle.putDouble(IntentConstant.TOTAL_PRICE.name, totalPrice)
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name))
    startFragmentOrderActivity(FragmentType.ADD_CUSTOMER, bundle, isResult = true)
  }

  private fun getItemList(fpTag : String?, clientId : String?) {
    showProgress(context?.getString(R.string.loading))
    viewModel?.getProductItems(fpTag, clientId, 0)?.observeOnce(viewLifecycleOwner, Observer {

      hideProgress()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }

      if (it.isSuccess()) {
        val resp = (it.arrayResponse as? Array<ProductItem>)
        productList = if (resp.isNullOrEmpty().not()) resp?.toCollection(ArrayList()) else ArrayList()

        if (productList?.size!! > 0) {
          binding?.tvNoProducts?.visibility = View.GONE
          binding?.productRecycler?.visibility = View.VISIBLE
          setAdapterOrderList(productList!!)
        } else {
          binding?.tvNoProducts?.visibility = View.VISIBLE
          binding?.productRecycler?.visibility = View.GONE
        }
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
    when (actionType) {
      RecyclerViewActionType.PRODUCT_ITEM_ADD.ordinal -> {
        val product = item as? ProductItem
        productList?.get(position)?.productQuantityAdded = (productList?.get(position)?.productQuantityAdded ?: 0) + 1
        itemsAdapter?.notifyDataSetChanged()

        totalPrice = totalPrice.plus(product?.Price ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.CurrencyCode ?: "INR" } $totalPrice"

        if (binding?.layoutTotalPricePanel?.visibility == View.GONE) {
          binding?.layoutTotalPricePanel?.visibility = View.VISIBLE
        }
        totalCartItems += 1
        selectedProductList?.add(product!!)
      }

      RecyclerViewActionType.PRODUCT_ITEM_INCREASE_COUNT.ordinal -> {
        val product = item as? ProductItem
        productList?.get(position)?.productQuantityAdded = (productList?.get(position)?.productQuantityAdded ?: 0) + 1
        itemsAdapter?.notifyDataSetChanged()

        totalPrice = totalPrice.plus(product?.Price ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.CurrencyCode ?: "INR" } $totalPrice"
        totalCartItems += 1
      }

      RecyclerViewActionType.PRODUCT_ITEM_DECREASE_COUNT.ordinal -> {
        val product = item as? ProductItem
        productList?.get(position)?.productQuantityAdded = (productList?.get(position)?.productQuantityAdded ?: 0) - 1
        itemsAdapter?.notifyDataSetChanged()

        totalPrice = totalPrice.minus(product?.Price ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.CurrencyCode ?: "INR" } $totalPrice"

        totalCartItems -= 1

        if (totalCartItems == 0) {
          binding?.layoutTotalPricePanel?.visibility = View.GONE
          selectedProductList?.clear()
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val req = bundle?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
      if (req != null) {
        createOrderRequest = req
        itemsAdapter?.notifyDataSetChanged()
      }

      val shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_REINITIATE.name)
      if (shouldReInitiate != null && shouldReInitiate) {
         totalPrice = 0.0
         totalCartItems = 0
         selectedProductList?.clear()
         createOrderRequest = OrderInitiateRequest()
         binding?.layoutTotalPricePanel?.visibility = View.GONE
         getItemList(fpTag, PRODUCT_LIST_CLIENT_ID)
      }
    }
  }
}
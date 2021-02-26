package com.inventoryorder.ui.order.createorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentAddProductBinding
import com.inventoryorder.model.CLIENT_ID_1
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.ProductDetails
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.util.*
import kotlin.collections.ArrayList


class AddProductFragment : BaseInventoryFragment<FragmentAddProductBinding>(), RecyclerItemClickListener {

  private var productList: ArrayList<ProductItem> = ArrayList()
  private var finalProductList: ArrayList<ProductItem> = ArrayList()
  private var itemsAdapter: AppBaseRecyclerViewAdapter<ProductItem>? = null
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
    setOnClickListener(binding?.tvProceed)
    getItemList(fpTag, CLIENT_ID_1)
    binding?.edtSearch?.afterTextChanged { filterProduct(it) }
  }


  private fun filterProduct(str: String?) {
    binding?.tvNoProducts?.visibility = View.GONE
    binding?.productRecycler?.visibility = View.VISIBLE
    when {
      str.isNullOrEmpty().not() -> {
        val query = str!!.trim().toLowerCase(Locale.ROOT)
        productList.clear()
        productList.addAll(finalProductList)
        productList = productList.filter { it.getNameValue().startsWith(query) || it.getNameValue().contains(query) } as ArrayList<ProductItem>
        setAdapterOrderList(productList)
      }
      finalProductList.isNullOrEmpty().not() -> {
        productList.clear()
        productList.addAll(finalProductList)
        setAdapterOrderList(productList)
      }
      else -> {
        binding?.tvNoProducts?.visibility = View.VISIBLE
        binding?.productRecycler?.visibility = View.GONE
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvProceed -> onProceedClicked()
    }
  }

  private fun onProceedClicked() {
    val itemList = ArrayList<ItemsItem>()
    finalProductList.forEach { prod ->
      if (prod.productQuantityAdded > 0) {
        val productDetails = ProductDetails()
        productDetails.currencyCode = prod.getCurrencyCodeValue()
        productDetails.description = prod.Description
        productDetails.discountAmount = prod.DiscountAmount
        productDetails.imageUri = prod.ImageUri
        productDetails.isAvailable = prod.IsAvailable
        productDetails.name = prod.Name
        productDetails.price = prod.Price
        productDetails.isAvailable = prod.IsAvailable
        val item = ItemsItem(productDetails = productDetails, productOrOfferId = prod._id)
        item.quantity = prod.productQuantityAdded
        item.type = "PRODUCT"
        itemList.add(item)
      }
    }
    createOrderRequest.items = itemList
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name))
    startFragmentOrderActivity(FragmentType.ADD_CUSTOMER, bundle, isResult = true)
  }

  private fun getItemList(fpTag: String?, clientId: String?) {
    showProgress(context?.getString(R.string.loading))
    viewModel?.getProductItems(fpTag, clientId, 0)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        val resp = (it.arrayResponse as? Array<ProductItem>)
        finalProductList = if (resp.isNullOrEmpty().not()) resp!!.toCollection(ArrayList()) else ArrayList()
        if (finalProductList.isNotEmpty()) {
          productList.clear()
          productList.addAll(finalProductList)
          binding?.tvNoProducts?.visibility = View.GONE
          binding?.productRecycler?.visibility = View.VISIBLE
          setAdapterOrderList(productList)
        } else {
          binding?.tvNoProducts?.visibility = View.VISIBLE
          binding?.productRecycler?.visibility = View.GONE
        }
      } else showShortToast(it.message)
    })
  }

  private fun setAdapterOrderList(list: ArrayList<ProductItem>) {
    if (itemsAdapter == null) {
      binding?.productRecycler?.apply {
        itemsAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this@AddProductFragment)
        adapter = itemsAdapter
        itemsAdapter?.runLayoutAnimation(this)
      }
    } else itemsAdapter?.notify(list)
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PRODUCT_ITEM_ADD.ordinal -> {
        val product = item as? ProductItem
        val productItem = productList[position]
        productItem.productQuantityAdded = productItem.productQuantityAdded + 1
        finalProductList.firstOrNull { product?._id.equals(it._id) }?.productQuantityAdded = productItem.productQuantityAdded
        itemsAdapter?.notifyDataSetChanged()
        totalPrice = totalPrice.plus(product?.getPayablePrice() ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.getCurrencyCodeValue() ?: "INR"} $totalPrice"
        if (binding?.layoutTotalPricePanel?.visibility == View.GONE) {
          binding?.layoutTotalPricePanel?.visibility = View.VISIBLE
        }
        totalCartItems += 1
      }

      RecyclerViewActionType.PRODUCT_ITEM_INCREASE_COUNT.ordinal -> {
        val product = item as? ProductItem
        val productItem = productList[position]
        productItem.productQuantityAdded = productItem.productQuantityAdded + 1
        finalProductList.firstOrNull { product?._id.equals(it._id) }?.productQuantityAdded = productItem.productQuantityAdded

        itemsAdapter?.notifyDataSetChanged()
        totalPrice = totalPrice.plus(product?.getPayablePrice() ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.getCurrencyCodeValue() ?: "INR"} $totalPrice"
        totalCartItems += 1
      }

      RecyclerViewActionType.PRODUCT_ITEM_DECREASE_COUNT.ordinal -> {
        val product = item as? ProductItem
        val productItem = productList[position]
        productItem.productQuantityAdded = productItem.productQuantityAdded - 1
        finalProductList.firstOrNull { product?._id.equals(it._id) }?.productQuantityAdded = productItem.productQuantityAdded
        itemsAdapter?.notifyDataSetChanged()
        totalPrice = totalPrice.minus(product?.getPayablePrice() ?: 0.0)
        binding?.tvItemTotalPrice?.text = "${product?.getCurrencyCodeValue() ?: "INR"} $totalPrice"
        totalCartItems -= 1
        if (totalCartItems == 0) binding?.layoutTotalPricePanel?.visibility = View.GONE
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val req = bundle?.getSerializable(IntentConstant.ORDER_REQUEST.name) as? OrderInitiateRequest
      if (req != null) {
        totalPrice = 0.0
        totalCartItems = 0
        createOrderRequest = req
        createOrderRequest.items?.forEach { prodAdd ->
          totalPrice+=prodAdd.getPayablePriceAmount()
          finalProductList.forEach lit@{
            if (prodAdd.productOrOfferId.equals(it._id)) {
              it.productQuantityAdded = prodAdd.quantity
              totalCartItems+=prodAdd.quantity
              return@lit
            }
          }
        }
        productList.clear()
        productList.addAll(finalProductList)
        itemsAdapter?.notifyDataSetChanged()
      }
      val shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_REINITIATE.name) ?: false
      if (shouldReInitiate) {
        totalPrice = 0.0
        totalCartItems = 0
        createOrderRequest = OrderInitiateRequest()
        binding?.layoutTotalPricePanel?.visibility = View.GONE
        getItemList(fpTag, CLIENT_ID_1)
      }
    }
  }
}
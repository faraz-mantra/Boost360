package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ItemsItem(
  @SerializedName("Type")
  var type: String = "",
  @SerializedName("ProductDetails")
  var productDetails: ProductDetails? = null,
  @SerializedName("Quantity")
  var quantity: Int = 0,
  @SerializedName("ProductOrOfferId")
  var productOrOfferId: String? = null,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItem: Int = RecyclerViewItemType.PRODUCT_ITEM_SELECTED.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getActualPriceAmount(): Double {
    return productDetails?.getActualPrice()?.times(quantity) ?: 0.0
  }

  fun getPayablePriceAmount(): Double {
    return productDetails?.getPayablePrice()?.times(quantity) ?: 0.0
  }

  fun getTotalDisPriceAmount(): Double {
    return productDetails?.getDiscountAmount()?.times(quantity) ?: 0.0
  }
}
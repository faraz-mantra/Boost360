package com.inventoryorder.model.ordersdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ItemN(
    val ActualPrice: Double? = null,
    val Product: ProductN? = null,
    val Quantity: Int? = null,
    val SalePrice: Double? = null,
    val ShippingCost: Double? = null,
    val Type: String? = null
) : AppBaseRecyclerViewItem, Serializable {

  var recyclerViewType = RecyclerViewItemType.ITEM_ORDER_DETAILS.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }
}
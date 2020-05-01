package com.inventoryorder.model.ordersdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class OrderItem(
    val BillingDetails: BillingDetails? = null,
    val BuyerDetails: BuyerDetails? = null,
    val CancellationDetails: CancellationDetails? = null,
    val CreatedOn: String? = null,
    val InventoryDetails: Any? = null,
    val IsArchived: Boolean? = null,
    val Items: ArrayList<ItemX>? = null,
    val LogisticsDetails: LogisticsDetails? = null,
    val Mode: String? = null,
    val OrderAmountMatch: Boolean? = null,
    val PaymentDetails: PaymentDetails? = null,
    val ReferenceNumber: String? = null,
    val RefundDetails: Any? = null,
    val SellerDetails: SellerDetails? = null,
    val SettlementDetails: Any? = null,
    val Status: String? = null,
    val UpdatedOn: String? = null,
    val _id: String? = null
) : AppBaseRecyclerViewItem, Serializable {

  var recyclerViewType = RecyclerViewItemType.INVENTORY_ORDER_ITEM.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun status(): String {
    return Status?.toLowerCase() ?: ""
  }

  fun referenceNumber(): String {
    return ReferenceNumber?.trim()?.toLowerCase() ?: ""
  }

  fun getTitles(): String {
    var title = ""
    Items?.forEachIndexed { index, item ->
      if (index < 3) title += takeIf { index != 0 }?.let { "\n${item.Product?.Name?.trim()}" } ?: (item.Product?.Name?.trim())
    }
    return title
  }

  fun getLoaderItem(): OrderItem {
    this.recyclerViewType = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }
}

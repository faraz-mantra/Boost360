package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.product.Product
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ItemN(
    var ActualPrice: Double? = null,
    var Product: ProductN? = null,
    var product_detail: Product? = null,
    val Quantity: Int? = null,
    val SalePrice: Double? = null,
    val ShippingCost: Double? = null,
    val Type: String? = null
) : AppBaseRecyclerViewItem, Serializable {

  var recyclerViewType = RecyclerViewItemType.ITEM_ORDER_DETAILS.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun product(): ProductN {
    return Product ?: ProductN()
  }

  fun scheduledStartDate(): String? {
    val scheduledDate = getScheduledDate()
    return if (scheduledDate.isNullOrEmpty().not()) {
      return "${scheduledDate}T${Product?.extraItemProductConsultation()?.startTime()}:00.000Z"
    } else ""
  }

  fun scheduledEndDate(): String? {
    val scheduledDate = getScheduledDate()
    return if (scheduledDate.isNullOrEmpty().not()) {
      return "${scheduledDate}T${Product?.extraItemProductConsultation()?.endTime()}:00.000Z"
    } else ""
  }

  fun getScheduledDate(): String? {
    val extraConsultation = Product?.extraItemProductConsultation()
    return if (extraConsultation != null) {
      var dateString = DateUtils.parseDate(extraConsultation.scheduledDateTime, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_YYYY_MM_DD)
      if (dateString.isNullOrEmpty()) dateString = DateUtils.parseDate(extraConsultation.scheduledDateTime, DateUtils.FORMAT_SERVER_1_DATE, DateUtils.FORMAT_YYYY_MM_DD)
      dateString ?: ""
    } else ""
  }
}
package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.product.Product
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

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

  fun getFormattedScheduleDate(): String? {
    val extraConsultation = Product?.extraItemProductConsultation()
    return if (extraConsultation != null) {
      var dateString = DateUtils.parseDate(extraConsultation.scheduledDateTime, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_DD_MM_YYYY)
      if (dateString.isNullOrEmpty()) dateString = DateUtils.parseDate(extraConsultation.scheduledDateTime, DateUtils.FORMAT_SERVER_1_DATE, DateUtils.FORMAT_YYYY_MM_DD)
      dateString ?: ""
    } else ""
  }

  fun getScheduleDateAndTime() : String? {
    val scheduledDate = getFormattedScheduleDate()
    val sdf = SimpleDateFormat(DateUtils.FORMAT_DD_MM_YYYY)

    val date = sdf.parse(scheduledDate)

    var dateDiff = getDateDifference(date)
    if (dateDiff.isEmpty()) dateDiff = scheduledDate ?: ""

    return "$dateDiff ${DateUtils.parseDate(Product?.extraItemProductConsultation()?.startTime(), DateUtils.FORMAT_HH_MM, DateUtils.FORMAT_HH_MM_A)}-${DateUtils.parseDate(Product?.extraItemProductConsultation()?.endTime(), DateUtils.FORMAT_HH_MM, DateUtils.FORMAT_HH_MM_A)}"
  }

  private fun getDateDifference(date: Date) : String {

    var cal = Calendar.getInstance()
    cal.time = date

    val today = Calendar.getInstance()

    return if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
      "Today"
    } else  if (cal.get(Calendar.DAY_OF_YEAR)  == today.get(Calendar.DAY_OF_YEAR) + 1 && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
      "Tomorrow"
    } else  if (cal.get(Calendar.DAY_OF_YEAR)  == today.get(Calendar.DAY_OF_YEAR) - 1 && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
      "Yesterday"
    } else {
      ""
    }
  }

  private fun convertTime24to12hrFormat(time : String) {

  }
}
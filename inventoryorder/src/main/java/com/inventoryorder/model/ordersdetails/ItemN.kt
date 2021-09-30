package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.product.Product
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable
import java.util.*

data class ItemN(
  var ActualPrice: Double? = null,
  var Product: ProductN? = null,
  var product_detail: Product? = null,
  val Quantity: Int? = null,
  val SalePrice: Double? = null,
  val ShippingCost: Double? = null,
  val Type: String? = null,
  val TaxDetails: Any? = null,
  val TotalSalePrice: Double? = null,
  val OffersApplied: Any? = null,
  val FeedbackDetails: Any? = null,
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
      var dateString = parseDate(
        extraConsultation.scheduledDateTime,
        DateUtils.FORMAT_SERVER_DATE,
        DateUtils.FORMAT_YYYY_MM_DD
      )
      if (dateString.isNullOrEmpty()) dateString = parseDate(
        extraConsultation.scheduledDateTime,
        DateUtils.FORMAT_SERVER_1_DATE,
        DateUtils.FORMAT_YYYY_MM_DD
      )
      dateString ?: ""
    } else ""
  }

  private fun getFormattedScheduleDate(): String? {
    val extraConsultation = Product?.extraItemProductConsultation()
    return if (extraConsultation != null) {
      var dateString = parseDate(
        extraConsultation.scheduledDateTime,
        DateUtils.FORMAT_SERVER_DATE,
        DateUtils.FORMAT_DD_MM_YYYY_N
      )
      if (dateString.isNullOrEmpty()) dateString = parseDate(
        extraConsultation.scheduledDateTime,
        DateUtils.FORMAT_SERVER_1_DATE,
        DateUtils.FORMAT_DD_MM_YYYY_N
      )
      dateString ?: ""
    } else ""
  }

  fun getScheduleDateAndTime(): String? {
    return try {
      val scheduledDate = getFormattedScheduleDate()
      val date = scheduledDate?.parseDate(DateUtils.FORMAT_DD_MM_YYYY_N)
      var dateDiff = date?.getDateDifference()
      if (dateDiff.isNullOrEmpty()) dateDiff = scheduledDate ?: ""
      "$dateDiff ${
        parseDate(
          Product?.extraItemProductConsultation()?.startTime(),
          DateUtils.FORMAT_HH_MM,
          DateUtils.FORMAT_HH_MM_A
        )
      }-${
        parseDate(
          Product?.extraItemProductConsultation()?.endTime(),
          DateUtils.FORMAT_HH_MM,
          DateUtils.FORMAT_HH_MM_A
        )
      }"
    } catch (e: Exception) {
      ""
    }
  }

  fun getAptSpaExtraDetail(): SpaAppointmentStaff? {
    return Product?.extraItemProductConsultation()?.getSpaAptStaffDetail()
  }

  fun getScheduleDateAndTimeSpa(): String? {
    return try {
      val extraDataSpa = getAptSpaExtraDetail()
      val scheduledDate = parseDate(
        extraDataSpa?.scheduledDateTime ?: "",
        DateUtils.FORMAT_YYYY_MM_DD,
        DateUtils.FORMAT_DD_MM_YYYY_N
      )
      val date = scheduledDate?.parseDate(DateUtils.FORMAT_DD_MM_YYYY_N)
      var dateDiff = date?.getDateDifference()
      if (dateDiff.isNullOrEmpty()) dateDiff = scheduledDate ?: ""
      "$dateDiff ${extraDataSpa?.startTimeRemoveAMPM()}-${extraDataSpa?.endTime()}"
    } catch (e: Exception) {
      ""
    }
  }

  private fun Date.getDateDifference(): String {
    val cal = Calendar.getInstance()
    cal.time = this
    val today = Calendar.getInstance()
    return if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == today.get(
        Calendar.YEAR
      )
    ) {
      "Today"
    } else if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1 && cal.get(
        Calendar.YEAR
      ) == today.get(Calendar.YEAR)
    ) {
      "Tomorrow"
    } else if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 && cal.get(
        Calendar.YEAR
      ) == today.get(Calendar.YEAR)
    ) {
      "Yesterday"
    } else {
      ""
    }
  }
}
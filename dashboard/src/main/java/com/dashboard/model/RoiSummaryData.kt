package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.inventoryorder.model.ordersummary.OrderSummaryModel

class RoiSummaryData(
  var title: String? = null,
  var dayTitle: String? = null,
  var value: String? = null,
  var icon1: Int? = null,
  var isRupeeSymbols: Boolean = false,
  var type: String? = null,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.ROI_SUMMARY_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(
    enquiry: String,
    totalCalls: String,
    sellerOrder: OrderSummaryModel?,
    type: String?
  ): ArrayList<RoiSummaryData> {
    val list = ArrayList<RoiSummaryData>()
    when (type) {
      "DOC" -> {
        list.add(
          RoiSummaryData(
            title = "Patient\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Appointments\nbooked",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
//        list.add(RoiSummaryData(title = "Online Video\nConsultations", value = "-", icon1 = R.drawable.ic_meeting_conslt_d, type = RoiType.CONSULTATION.name))
        list.add(
          RoiSummaryData(
            title = "Appointment\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value ="0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "HOS" -> {
        list.add(
          RoiSummaryData(
            title = "Patient\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Appointments\nbooked",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
//        list.add(RoiSummaryData(title = "Online Video\nConsultations", value = "-", icon1 = R.drawable.ic_meeting_conslt_d, type = RoiType.CONSULTATION.name))
        list.add(
          RoiSummaryData(
            title = "Appointment\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value ="0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "SPA", "SAL" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "In-Store\nappointments",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Appointment\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "MFG" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Customer\norders",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Order\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "RTL" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Customer\norders",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Order\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "SVC" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Customer\nappointments",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Appointment\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "EDU" -> {
        list.add(
          RoiSummaryData(
            title = "Student\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Admission\nrequests",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
//        list.add(RoiSummaryData(title = "Appointment\nWorth", value =  sellerOrder?.getTotalNetAmount(), icon1 = R.drawable.ic_meeting_apt_d, isRupeeSymbols = true, type = RoiType.APT_ORDER_WORTH.name))
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "HOT" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Room\nbookings",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Booking\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "CAF" -> {
        list.add(
          RoiSummaryData(
            title = "Customer\nenquiries",
            value = enquiry,
            icon1 = R.drawable.ic_chat_enquiry_d,
            type = RoiType.ENQUIRY.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Tracked\ncalls",
            value = totalCalls,
            icon1 = R.drawable.ic_track_call_d,
            type = RoiType.TRACK_CALL.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Food\norders",
            value = sellerOrder?.getTotalOrders(),
            icon1 = R.drawable.ic_meeting_apt_d,
            type = RoiType.APT_ORDER.name
          )
        )
        list.add(
          RoiSummaryData(
            title = "Order\nworth",
            value = sellerOrder?.getTotalNetAmount(),
            icon1 = R.drawable.ic_meeting_apt_d,
            isRupeeSymbols = true,
            type = RoiType.APT_ORDER_WORTH.name
          )
        )
//        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
    }
    return list
  }

  enum class RoiType {
    ENQUIRY, TRACK_CALL, APT_ORDER, CONSULTATION, APT_ORDER_WORTH, COLLECTION_WORTH;

    companion object {
      fun fromName(name: String?): RoiType? = values().firstOrNull { it.name == name }
    }
  }
}

fun ArrayList<RoiSummaryData>.isAllDataZero(): Boolean {
  var isAllZero = true
  this.forEach { if ((it.value.isNullOrEmpty() || it.value.equals("0")).not()) isAllZero = false }
  return isAllZero
}

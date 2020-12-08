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

  fun getData(enquiry: String, totalCalls: String, sellerOrder: OrderSummaryModel?, type: String?): ArrayList<RoiSummaryData> {
    val list = ArrayList<RoiSummaryData>()
    when (type) {
      "DOC" -> {
        list.add(RoiSummaryData(title = "Patient\nEnquiries", value = enquiry, icon1 = R.drawable.ic_chat_enquiry_d, type = RoiType.ENQUIRY.name))
        list.add(RoiSummaryData(title = "Tracked\nCalls", value = totalCalls, icon1 = R.drawable.ic_track_call_d, type = RoiType.TRACK_CALL.name))
        list.add(RoiSummaryData(title = "In-Clinic\nAppointments", value = sellerOrder?.getTotalNetAmount() ?: "0", icon1 = R.drawable.ic_meeting_apt_d, type = RoiType.APT_ORDER.name))
        list.add(RoiSummaryData(title = "Online Video\nConsultations", value = "0", icon1 = R.drawable.ic_meeting_conslt_d, type = RoiType.CONSULTATION.name))
        list.add(RoiSummaryData(title = "Appointment\nWorth", value = "0K", icon1 = R.drawable.ic_meeting_apt_d, isRupeeSymbols = true, type = RoiType.APT_ORDER_WORTH.name))
        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0k", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "SPA" -> {
        list.add(RoiSummaryData(title = "Customer\nEnquiries", value = enquiry, icon1 = R.drawable.ic_chat_enquiry_d, type = RoiType.ENQUIRY.name))
        list.add(RoiSummaryData(title = "Tracked\nCalls", value = totalCalls, icon1 = R.drawable.ic_track_call_d, type = RoiType.TRACK_CALL.name))
        list.add(RoiSummaryData(title = "In-Clinic\nAppointments", value = sellerOrder?.getTotalNetAmount() ?: "0", icon1 = R.drawable.ic_meeting_apt_d, type = RoiType.APT_ORDER.name))
        list.add(RoiSummaryData(title = "Appointment\nWorth", value = "0K", icon1 = R.drawable.ic_meeting_apt_d, isRupeeSymbols = true, type = RoiType.APT_ORDER_WORTH.name))
        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0k", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
      "MFG" -> {
        list.add(RoiSummaryData(title = "Customer\nEnquiries", value = enquiry, icon1 = R.drawable.ic_chat_enquiry_d, type = RoiType.ENQUIRY.name))
        list.add(RoiSummaryData(title = "Tracked\nCalls", value = totalCalls, icon1 = R.drawable.ic_track_call_d, type = RoiType.TRACK_CALL.name))
        list.add(RoiSummaryData(title = "Customer\nOrders", value = sellerOrder?.getTotalNetAmount() ?: "0", icon1 = R.drawable.ic_meeting_apt_d, type = RoiType.APT_ORDER.name))
        list.add(RoiSummaryData(title = "Order\nWorth", value = "0K", icon1 = R.drawable.ic_meeting_apt_d, isRupeeSymbols = true, type = RoiType.APT_ORDER_WORTH.name))
        list.add(RoiSummaryData(title = "Collection\nWorth (INR)", value = "0k", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true, type = RoiType.COLLECTION_WORTH.name))
      }
    }
    return list
  }

  enum class RoiType {
    ENQUIRY, TRACK_CALL, APT_ORDER, CONSULTATION, APT_ORDER_WORTH, COLLECTION_WORTH;
  }
}
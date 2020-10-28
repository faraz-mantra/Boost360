package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class RoiSummaryData(
    var title: String? = null,
    var dayTitle: String? = null,
    var amount: String? = null,
    var icon1: Int? = null,
    var isRupeeSymbols: Boolean = false
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.ROI_SUMMARY_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<RoiSummaryData> {
    val list = ArrayList<RoiSummaryData>()
    list.add(RoiSummaryData(title = "Customer Enquiries", dayTitle = "2 today", amount = "1,579", icon1 = R.drawable.ic_chat_enquiry_d))
    list.add(RoiSummaryData(title = "Tracked\nCalls", dayTitle = "1 today", amount = "1,579", icon1 = R.drawable.ic_track_call_d))
    list.add(RoiSummaryData(title = "In-Clinic Appointments", amount = "1,579", icon1 = R.drawable.ic_meeting_apt_d))
    list.add(RoiSummaryData(title = "Online Video Consultations", amount = "1,579", icon1 = R.drawable.ic_meeting_conslt_d))
    list.add(RoiSummaryData(title = "Appointment Worth", dayTitle = "₹2,040 today", amount = "54.1k", icon1 = R.drawable.ic_meeting_apt_d, isRupeeSymbols = true))
    list.add(RoiSummaryData(title = "Collection Worth (INR)", amount = "34.5k", icon1 = R.drawable.ic_meeting_conslt_d, isRupeeSymbols = true))
    return list
  }
}
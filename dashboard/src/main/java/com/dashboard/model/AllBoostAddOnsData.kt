package com.dashboard.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class AllBoostAddOnsData(
    var title: String? = null,
    var subTitle: String? = null,
    var manageBusinessList: ArrayList<ManageBusinessData>? = null,
    var isLastSeen: Boolean = false,
    var isExpend: Boolean = true,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.ALL_BOOST_ADD_ONS_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<AllBoostAddOnsData> {
    val list = ArrayList<AllBoostAddOnsData>()
    list.add(AllBoostAddOnsData(title = "Last Seen", manageBusinessList = ManageBusinessData().getData(), isLastSeen = true))
    list.add(AllBoostAddOnsData(title = "Customer Orders & Enquiries", subTitle = "One place to manage all your patient interactions.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Content Management", subTitle = "Engage with your existing and potential customer with these tools.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Catalogue and Team Listing", subTitle = "Manage your services, staff and their appointment schedule.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Business Profile", subTitle = "Manage your services, staff and their appointment schedule.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Payment Collection Setup", subTitle = "Configure how and where you collect your customer payments.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Marketing Tools", subTitle = "Tools to help you increase your brand awareness and reach.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Realtime Analytics", subTitle = "Live analytics to help you know how your business is doing.", manageBusinessList = ManageBusinessData().getData()))
    list.add(AllBoostAddOnsData(title = "Business Support", subTitle = "We’re always there to help you, just tap and reach out to us.", manageBusinessList = ManageBusinessData().getData()))
    return list
  }
}
package com.dashboard.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

data class BusinessSetupHighData(
  var title1: String? = null,
  var siteVisitor: Specification? = null,
  var booking: Specification? = null,
  var enquiry: Specification? = null,
  var score: Int? = null,
  var type: String? = null,
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_SETUP_HIGH_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(
    score: Int,
    visitor: String,
    order: String,
    orderText: String = "Bookings",
    enquiry: String
  ): ArrayList<BusinessSetupHighData> {
    val list = ArrayList<BusinessSetupHighData>()
    list.add(
      BusinessSetupHighData(
        title1 = "Business\nSummary",
        siteVisitor = Specification(
          "Unique visits",
          visitor,
          BusinessClickEvent.WEBSITE_VISITOR.name
        ),
        booking = Specification(orderText, order, BusinessClickEvent.ODER_APT.name),
        enquiry = Specification("Enquiries", enquiry, BusinessClickEvent.ENQUIRIES.name),
        type = ActiveViewType.IS_BUSINESS_UPDATE.name
      )
    )
    list.add(
      BusinessSetupHighData(
        title1 = "Website Readiness Score: ",
        score = score,
        type = ActiveViewType.IS_PROGRESS.name
      )
    )
    return list
  }

  enum class ActiveViewType {
    IS_BUSINESS_UPDATE, IS_PROGRESS
  }

  enum class BusinessClickEvent {
    WEBSITE_VISITOR, ODER_APT, ENQUIRIES;

    companion object {
      fun fromName(name: String?): BusinessClickEvent? = values().firstOrNull { it.name == name }
    }
  }
}

data class Specification(
  var title: String? = null,
  var value: String? = null,
  var clickType: String? = null,
) : Serializable, AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return RecyclerViewItemType.BUSINESS_SETUP_HIGH_ITEM_VIEW.getLayout()
  }
}

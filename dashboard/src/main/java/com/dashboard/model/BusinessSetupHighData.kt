package com.dashboard.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

data class BusinessSetupHighData(
    var title1: String? = null,
    var title2: String? = null,
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

  fun getData(score: Int): ArrayList<BusinessSetupHighData> {
    val list = ArrayList<BusinessSetupHighData>()
    list.add(BusinessSetupHighData(title1 = "Business", title2 = "Update", siteVisitor = Specification("Site visitors", "3,45,890"), booking = Specification("Bookings", "3,400"), enquiry = Specification("Enquiries", "2,105"), type = ActiveViewType.IS_BUSINESS_UPDATE.name))
    list.add(BusinessSetupHighData(title1 = "Digital readiness score: $score%", score = score, type = ActiveViewType.IS_PROGRESS.name))
    return list
  }

  enum class ActiveViewType {
    IS_BUSINESS_UPDATE, IS_PROGRESS
  }
}

data class Specification(
    var title: String? = null,
    var value: String? = null,
) : Serializable

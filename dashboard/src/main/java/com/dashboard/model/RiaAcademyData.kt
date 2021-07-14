package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class RiaAcademyData(
  var title: String? = null,
  var dayTitle: String? = null,
  var amount: String? = null,
  var icon1: Int? = null
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.RIA_ACADEMY_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<RiaAcademyData> {
    val list = ArrayList<RiaAcademyData>()
    list.add(RiaAcademyData(title = "Website", icon1 = R.drawable.ic_website_d))
    list.add(RiaAcademyData(title = "GMB", icon1 = R.drawable.ic_google_maps_n))
    list.add(RiaAcademyData(title = "GMB", icon1 = R.drawable.ic_google_maps_n))
    return list
  }
}
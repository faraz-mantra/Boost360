package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class GstDetailModel(
    var title: String? = null,
    var value: String? = null,
    var recyclerViewItem: Int = RecyclerViewItemType.GST_DETAILS_VIEW.getLayout()
) : AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun gstData(): ArrayList<GstDetailModel> {
    val list = ArrayList<GstDetailModel>()
    list.add(GstDetailModel(value = "0.0"))
    list.add(GstDetailModel(value = "3.0"))
    list.add(GstDetailModel(value = "5.0"))
    list.add(GstDetailModel(value = "12.0"))
    list.add(GstDetailModel(value = "18.0"))
    list.add(GstDetailModel(value = "28.0"))
    return list
  }
}
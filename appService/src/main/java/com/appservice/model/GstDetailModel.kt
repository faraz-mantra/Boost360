package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.utils.toArrayList
import java.io.Serializable

data class GstDetailModel(
  var title: String? = null,
  var value: String? = null,
  var recyclerViewItem: Int = RecyclerViewItemType.GST_DETAILS_VIEW.getLayout()
) : AppBaseRecyclerViewItem, Serializable {

  var isSelected = false

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun gstData(): ArrayList<GstDetailModel> {
    val list = ArrayList<GstDetailModel>()
    list.add(GstDetailModel(value = "0"))
    list.add(GstDetailModel(value = "3"))
    list.add(GstDetailModel(value = "5"))
    list.add(GstDetailModel(value = "12"))
    list.add(GstDetailModel(value = "18"))
    list.add(GstDetailModel(value = "28"))
    return list
  }

  fun gstDataNew(gstSlab: Int): ArrayList<GstDetailModel> {
    val list = ArrayList<GstDetailModel>()
    list.add(GstDetailModel(title = "0%", value = "0", recyclerViewItem = RecyclerViewItemType.GST_SLAB_SETTING.getLayout()))
    list.add(GstDetailModel(title = "5%", value = "5", recyclerViewItem = RecyclerViewItemType.GST_SLAB_SETTING.getLayout()))
    list.add(GstDetailModel(title = "12%", value = "12", recyclerViewItem = RecyclerViewItemType.GST_SLAB_SETTING.getLayout()))
    list.add(GstDetailModel(title = "18%", value = "18", recyclerViewItem = RecyclerViewItemType.GST_SLAB_SETTING.getLayout()))
    list.add(GstDetailModel(title = "28%", value = "28", recyclerViewItem = RecyclerViewItemType.GST_SLAB_SETTING.getLayout()))
    return ArrayList(list.map { it.isSelected = (it.value?.toIntOrNull() ?: 0 == gstSlab);it })
  }
}
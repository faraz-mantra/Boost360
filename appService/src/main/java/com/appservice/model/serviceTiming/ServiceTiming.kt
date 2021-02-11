package com.appservice.model.serviceTiming

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceTiming(
    @SerializedName("Day")
    var day: String? = null,
    @SerializedName("Time")
    var time: ServiceTime? = null,
    var isToggle: Boolean = false,
    var appliedOnPosition: Int? = null,
) : Serializable, AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return RecyclerViewItemType.SERVICE_TIMING_ITEM_VIEW.getLayout()
  }

  fun getTimeData(): ServiceTime {
    this.time = this.time ?: ServiceTime()
    return time!!
  }

  fun getEmptyDataServiceTiming(): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEachIndexed { index, s -> list.add(ServiceTiming(s, isToggle = (index == 0))) }
    return list
  }

  fun getRequestEmptyTiming(): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEach { list.add(ServiceTiming(it, time = ServiceTime("", ""))) }
    return list
  }
}
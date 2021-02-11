package com.appservice.model.serviceTiming

import com.appservice.constant.RecyclerViewItemType
import com.appservice.model.serviceTiming.ServiceTiming.WeekdayValue.Companion.fromFullName
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

  fun getEmptyDataServiceTiming(isEdit: Boolean): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEachIndexed { index, s -> list.add(ServiceTiming(s, isToggle = (isEdit.not() && index == 0))) }
    return list
  }

  fun getRequestEmptyTiming(): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEach { list.add(ServiceTiming(it, time = ServiceTime("", ""))) }
    return list
  }

  fun getStringActive(list: ArrayList<ServiceTiming>?): String {
    var txtDays = ""
    list?.forEach {
      if (it.isToggle) {
        val value = fromFullName(it.day) ?: return@forEach
        txtDays = if (txtDays.isNotEmpty()) "$txtDays, ${value.sortName}" else value.sortName
      }
    }
    return txtDays
  }

  enum class WeekdayValue(var fullName: String, var sortName: String) {
    MONDAY("Monday", "Mon"), TUESDAY("Tuesday", "Tue"), WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thu"), FRIDAY("Friday", "Fri"), SATURDAY("Saturday", "Sat"),
    SUNDAY("Sunday", "Sun");

    companion object {
      fun fromFullName(fullName: String?): WeekdayValue? = values().firstOrNull { it.fullName.equals(fullName, false) }
    }
  }
}
package com.appservice.model.serviceTiming

import com.appservice.constant.RecyclerViewItemType
import com.appservice.model.serviceTiming.ServiceTiming.WeekdayValue.Companion.fromFullName
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.android.libraries.places.api.model.Place
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceTiming(
    @SerializedName("Day")
    var day: String? = null,
    @SerializedName("Time")
    var time: ServiceTime? = null,
    var isToggle: Boolean = false,
    var appliedOnPosition: Int? = null,
    var businessTiming: BusinessHourTiming? = null,
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.SERVICE_TIMING_ITEM_VIEW.getLayout()
  }

  fun getTimeData(): ServiceTime {
    this.time = this.time ?: ServiceTime()
    return time!!
  }

  fun isOpenDay(): Boolean {
    return ((businessTiming?.startTime.isNullOrEmpty() || businessTiming?.startTime.equals("00:00") || businessTiming?.startTime.equals("00")).not() &&
        (businessTiming?.endTime.isNullOrEmpty() || businessTiming?.endTime.equals("00:00") || businessTiming?.endTime.equals("00")).not())
  }

  fun isValidTime(): Boolean {
    return ((time?.from.isNullOrEmpty() || time?.from.equals("00:00") || time?.from.equals("00")).not() &&
        (time?.to.isNullOrEmpty() || time?.to.equals("00:00") || time?.to.equals("00")).not())

  }
  fun getEmptyDataServiceTiming(isEdit: Boolean): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEach { list.add(ServiceTiming(it)) }
    return list
  }

  fun getRequestEmptyTiming(): ArrayList<ServiceTiming> {
    val list = ArrayList<ServiceTiming>()
    val days = arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    days.forEach { list.add(ServiceTiming(it, time = ServiceTime("00:00", "00:00"))) }
    return list
  }

  fun getStringActive(list: ArrayList<ServiceTiming>?): String {
    var txtDays = ""
    list?.forEach {
      if (it.isToggle && it.isOpenDay()) {
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
      fun fromFullName(fullName: String?): WeekdayValue? = values().firstOrNull { it.fullName.equals(fullName, true) }
    }
  }
}

class BusinessHourTiming(
    var startTime: String = "",
    var endTime: String = "",
) : Serializable

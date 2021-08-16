package com.inventoryorder.model.services

import com.framework.utils.DateUtils.FORMAT_HH_MM
import com.framework.utils.DateUtils.FORMAT_HH_MM_A
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.services.TimingModel.WeekDay.Companion.from
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable
import java.util.*

data class TimingModel(
  var dayName: String? = "",
  var availStartTime: String? = "",
  var availEndTime: String? = "",
  var breakStartTime: String? = "",
  var breakEndTime: String? = "",
  var isMarkOff: Boolean = false
) : AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return RecyclerViewItemType.WEEK_TIMING_SELECTED.getLayout()
  }

  fun isLast(): Boolean {
    return dayName == WeekDay.SATURDAY.name
  }

  fun getIsMarkOnText(): String {
    val availTime = if (availStartTime.isNullOrEmpty().not() && availEndTime.isNullOrEmpty().not())
      "$availStartTime to $availEndTime" else "$availStartTime$availEndTime"
    val breakTime = if (breakStartTime.isNullOrEmpty().not() && breakEndTime.isNullOrEmpty().not())
      "$breakStartTime to $breakEndTime" else "$breakStartTime$breakEndTime"
    val str = if (availTime.isNotEmpty() && breakTime.isNotEmpty()) "$availTime : BR - $breakTime"
    else if (availTime.isNotEmpty()) "$availTime : No BR" else ""
    return if (str.isNotEmpty()) "${from(dayName ?: "")?.day2} - $str" else ""
  }

  fun getIsMarkOffText(): String {
    return "${from(dayName ?: "")?.day1} has been marked as off"
  }

  fun getTiming(): String {
    return if (!isMarkOff) "#${getStartTime24()},${getEndTime24()}#" else ""
  }

  fun getDay(): String {
    return from(dayName ?: "")?.day1 ?: ""
  }

  private fun getStartTime24(): String {
    return parseDate(availStartTime, FORMAT_HH_MM_A, FORMAT_HH_MM) ?: ""
  }

  private fun getEndTime24(): String {
    return parseDate(availEndTime, FORMAT_HH_MM_A, FORMAT_HH_MM) ?: ""
  }


  enum class WeekDay(val day1: String, val day2: String) {
    TUESDAY("Tuesday", "Tues"),
    WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thur"),
    FRIDAY("Friday", "Fri"),
    SATURDAY("Saturday", "Sat"),
    SUNDAY("Sunday", "Sun"),
    MONDAY("Monday", "Mon");

    companion object {
      fun from(name: String): WeekDay? =
        values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == name.toLowerCase(Locale.ROOT) }
    }
  }
}
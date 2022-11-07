package com.dashboard.utils

import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.getCurrentTimeIn24Hour
import com.framework.utils.DateUtils.isBetweenValidTime
import com.framework.utils.DateUtils.parseDate
import java.util.*

fun UserSessionManager.getCurrentTimingsData(invokeData: ((isOpen: Boolean, day: String, timing: String) -> Unit)) {
  var key_start_time = ""
  var key_end_time = ""
  var day = ""
  when (Calendar.getInstance()[Calendar.DAY_OF_WEEK]) {
    Calendar.SUNDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME
      day = "Sunday"
    }
    Calendar.MONDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME
      day = "Monday"
    }
    Calendar.TUESDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME
      day = "Tuesday"
    }
    Calendar.WEDNESDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME
      day = "Wednesday"
    }
    Calendar.THURSDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME
      day = "Thursday"
    }
    Calendar.FRIDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME
      day = "Friday"
    }
    Calendar.SATURDAY -> {
      key_start_time = Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME
      key_end_time = Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME
      day = "Saturday"
    }
  }
  val startTime = this.getFPDetails(key_start_time)?.lowercase(Locale.ROOT)?.lowercase(Locale.ROOT)
  val endTime = this.getFPDetails(key_end_time)?.lowercase(Locale.ROOT)?.lowercase(Locale.ROOT)
  val startDateNew: Date? = startTime?.parseDate(DateUtils.FORMAT_HH_MMA)
  val endDateNew: Date? = endTime?.parseDate(DateUtils.FORMAT_HH_MMA)
  val currentDat: Date? = getCurrentTimeIn24Hour().parseDate(DateUtils.FORMAT_HH_MM)
  val timing = if (startTime.isNullOrEmpty().not() && endTime.isNullOrEmpty()) "$startTime - $endTime" else ""
  val isOpen = if (startDateNew != null && endDateNew != null && currentDat != null) isBetweenValidTime(startDateNew, endDateNew, currentDat) else false
  invokeData(isOpen, day, timing)
}
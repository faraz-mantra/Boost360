package com.appservice.ui.catalog.common

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppointmentModel(
    @field:SerializedName("day", alternate = ["Day"])
    var day: String? = null,
    var isViewEnabled: Boolean? = true,
    @field:SerializedName("timing", alternate = ["Timing"])
    var timeSlots: ArrayList<TimeSlot> = arrayListOf(),

    var isDataAppliedOnMyDay: Boolean? = false,
    var isTurnedOn: Boolean? = false,
    var isAppliedOnAllDays: Boolean? = false,
    var isAppliedOnAllDaysViewVisible: Boolean? = false,
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.SESSION_ITEM_VIEW.getLayout()
  }

  fun getDefaultModel(day: String?, isAppliedOnAllDays: Boolean, isAppliedOnAllDaysVisible: Boolean): AppointmentModel {
    val m = AppointmentModel()
    m.day = day
    m.isTurnedOn = false
    m.isAppliedOnAllDays = isAppliedOnAllDays
    m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible
    return m
  }

  private fun isDayTurnedOn() {
    timeSlots = ArrayList()
    this.isTurnedOn = true
    timeSlots.add(TimeSlot.getDefaultTimeSlotObject());
  }


  fun deleteSession(sessionIndex: Int) {
    timeSlots.removeAt(sessionIndex)

  }

  fun changeDayTurned(dayTurned: Boolean) {
    when {
      dayTurned -> isDayTurnedOn()
      else -> isDayTurnedOff()
    }
  }

  private fun isDayTurnedOff() {
    this.isTurnedOn = false
    timeSlots.clear()

  }

  fun addSession() {
    timeSlots.add(TimeSlot.getDefaultTimeSlotObject())
  }

  fun removeSession(index: Int) {
    timeSlots.removeAt(index);
  }

  fun removeApplyOnAllDays(data: AppointmentModel) {
    getDefaultTimings().forEach { if (it != data) it?.isTurnedOn = false }
  }

  companion object {
    fun getDefaultTimings(): ArrayList<AppointmentModel> {
      val list = ArrayList<AppointmentModel>()
      val monday = AppointmentModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = false);
      monday.isAppliedOnAllDaysViewVisible = true;
      list.add(monday);
      list.add(AppointmentModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
      list.add(AppointmentModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
      list.add(AppointmentModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
      list.add(AppointmentModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
      list.add(AppointmentModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
      list.add(AppointmentModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
      return list
    }
  }

  fun getStringStaffActive(list: ArrayList<AppointmentModel>?): String {
    val selectedDays = StringBuilder()
    list?.forEach { item ->
      if (item!=null && !item.day.isNullOrEmpty() && item.timeSlots.isNotEmpty()) {
        val value = WeekdayStaffValue.fromFullName(item.day) ?: return@forEach
        if (selectedDays.isNotEmpty()) selectedDays.append(", ")
        selectedDays.append(value.sortName)
      }
    }
    return selectedDays.toString()
  }

  fun getStringStaffActiveN(list: ArrayList<AppointmentModel>?): String {
    val selectedDays = StringBuilder()
    list?.forEach { item ->
      if (item!=null && item.isTurnedOn == true) {
        val value = WeekdayStaffValue.fromFullName(item.day) ?: return@forEach
        if (selectedDays.isNotEmpty()) selectedDays.append(", ")
        selectedDays.append(value.sortName)
      }
    }
    return selectedDays.toString()
  }

  enum class WeekdayStaffValue(var fullName: String, var sortName: String): Serializable {
    MONDAY("Monday", "Mon"), TUESDAY("Tuesday", "Tue"), WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thu"), FRIDAY("Friday", "Fri"), SATURDAY("Saturday", "Sat"),
    SUNDAY("Sunday", "Sun");

    companion object {
      fun fromFullName(fullName: String?): WeekdayStaffValue? = values().firstOrNull { it.fullName.equals(fullName, false) }
    }
  }
}


data class TimeSlot(
    @field:SerializedName("from", alternate = ["From"])
    var from: String? = null,
    @field:SerializedName("to", alternate = ["To"])
    var to: String? = null,
) : Serializable {

  companion object {
    fun getDefaultTimeSlotObject(): TimeSlot {
      val timeSlot = TimeSlot();
      timeSlot.from = "09:30AM"
      timeSlot.to = "07:00PM"
      return timeSlot;
    }
  }
}


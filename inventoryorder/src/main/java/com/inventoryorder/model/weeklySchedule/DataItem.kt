package com.inventoryorder.model.weeklySchedule

import android.util.Log
import com.framework.utils.DateUtils.FORMAT_HH_MM
import com.framework.utils.DateUtils.FORMAT_HH_MM_A
import com.framework.utils.DateUtils.parseDate
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.timeSlot.TimeSlotData
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class DataItem(
    @SerializedName("WebsiteId")
    val websiteId: String = "",
    @SerializedName("IsArchived")
    val isArchived: Boolean = false,
    @SerializedName("ActionId")
    val actionId: String = "",
    @SerializedName("doctorId")
    val doctorId: String = "",
    @SerializedName("timing")
    val timing: String = "",
    @SerializedName("UserId")
    val userId: String = "",
    @SerializedName("UpdatedOn")
    val updatedOn: String = "",
    @SerializedName("_id")
    val Id: String = "",
    @SerializedName("CreatedOn")
    val createdOn: String = "",
    @SerializedName("day")
    val day: String = ""
) : Serializable {

  fun getTimeSlot(duration: Long): ArrayList<TimeSlotData> {
    val timeSlotData = ArrayList<TimeSlotData>()
    if (timing.isNotEmpty() && DayName.fromValue(day) != null) {
      val splitTime = timing.split("#")
      if (splitTime.isNotEmpty()) {
        splitTime.forEachIndexed { index, time ->
          if ((index == 0 || index + 1 == splitTime.size).not()) {
            val timeList = time.trim().split(",")
            if (timeList.size >= 2) {
              val startTime = timeList[0].trim()
              val endTime = timeList[1].trim()
              if (startTime != "0" && endTime != "0") displayTimeSlots(startTime, endTime, duration, timeSlotData)
            }
          }
        }
      }
    }
    return timeSlotData
  }

  enum class DayName(val value: String) {
    SUNDAY("Sunday"), MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"), THURSDAY("Thursday"), FRIDAY("Friday"), SATURDAY("Saturday");

    companion object {
      fun fromValue(value: String): DayName? = values().firstOrNull { it.value.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  private fun displayTimeSlots(time1: String, time2: String, duration: Long, timeSlotData: ArrayList<TimeSlotData>) {
    try {
      val dateObj1: Date? = time1.parseDate(FORMAT_HH_MM)
      val dateObj2: Date? = time2.parseDate(FORMAT_HH_MM)
      if (dateObj1 != null && dateObj2 != null) {
        var dif = dateObj1.time
        while (dif < dateObj2.time) {
          val slot1 = Date(dif)
          dif += duration
          val slot2 = Date(dif)
          val startTimeSlot = slot1.parseDate(FORMAT_HH_MM_A)
          val endTimeSlot = slot2.parseDate(FORMAT_HH_MM_A)
          timeSlotData.add(TimeSlotData(day = day, startTime = startTimeSlot, endTime = endTimeSlot))
          Log.d("TAG", "Hour slot =  $startTimeSlot - $endTimeSlot")
        }
      }
    } catch (ex: Exception) {
      Log.e(DataItem::class.java.name, ex.localizedMessage)
    }
  }
}

fun isTimeBetweenTwoHours(startC: Calendar?, endC: Calendar?, testC: Calendar, startTime: Boolean, isCurrentDate: Boolean): Boolean {
  if (startC == null && endC == null) return false
  return try {
    when {
      isCurrentDate -> startC!! <= testC //(testC.after(startC) && testC.after(endC))
      startTime -> (startC!! <= testC) && (endC!! > testC)
      else -> (endC!! >= testC) && (startC!! < testC)
    }
  } catch (e: Exception) {
    Log.e(DataItem::class.java.name, e.localizedMessage)
    false
  }
}
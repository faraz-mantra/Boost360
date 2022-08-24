package com.framework.utils

import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object DateUtils {

  const val FORMAT_SERVER_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  const val FORMAT_SERVER_DATE1 = "yyyy-MM-dd'T'HH:mm:ss.SS"
  const val FORMAT_SERVER_1_DATE = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  const val FORMAT_SERVER_TO_LOCAL = "dd-MM-yyyy hh:mm a"
  const val FORMAT_SERVER_TO_LOCAL_1 = "dd-MM-yyyy HH:mm"
  const val FORMAT_SERVER_TO_LOCAL_2 = "EEE',' dd MMMM',' hh:mm a"
  const val FORMAT_SERVER_TO_LOCAL_3 = "MMM dd, yyyy 'at' hh:mm a";
  const val FORMAT_SERVER_TO_LOCAL_4 = "hh:mm a 'on' EEE',' dd MMM yyyy";
  const val FORMAT_SERVER_TO_LOCAL_5 = "EEE',' dd MMM yyyy";
  const val FORMAT_SERVER_TO_LOCAL_6 = "EEE',' dd MMMM"
  const val FORMAT_SERVER_TO_LOCAL_7 = "dd MMM yyyy, hh:mm a";
  const val FORMAT_DD_MM_YYYY = "dd-MM-yyyy"
  const val FORMAT1_DD_MM_YYYY = "dd MMM yyyy"
  const val FORMAT_DD_MM_YYYY_N = "dd/MM/yyyy"
  const val FORMAT_DD_MM_YYYY_hh_mm_ss = "dd-MM-yyyy HH:mm:ss"
  const val FORMAT__DD__MM__YYYY = "dd MM yyyy"
  const val FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
  const val FORMAT_HH_MM_SS_A = "hh:mm:ss a"
  const val FORMAT_HH_MM_SS = "HH:mm:ss"
  const val FORMAT_HH_MM = "HH:mm"
  const val FORMAT_HH_MM_A = "hh:mm a"
  const val FORMAT_HH_MMA = "hh:mma"
  const val FORMAT_H_MM_A = "h:mm a"
  const val SPA_DISPLAY_DATE = "EEE',' dd MMMM yyyy"
  const val KEYBOARD_DISPLAY_DATE = "EEE, MMMM dd, yyyy • hh:mm a"
  const val SPA_REVIEW_DATE_FORMAT = "EEE',' MMM dd, yyyy"
  private val dateFormater = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
  private val dateFormaterToday = SimpleDateFormat("HH:mm", Locale.getDefault())
  private val dateFormaterDate = SimpleDateFormat("HH:mm", Locale.getDefault())

  fun parseDate(timestamp: String, format: String?, locale: Locale = Locale.getDefault()): String? {
    if (!TextUtils.isEmpty(timestamp) && !TextUtils.isEmpty(format)) {
      val timeStamp = timestamp.toLong()
      val dateFormat: DateFormat = SimpleDateFormat(format, locale)
      return dateFormat.format(Date(timeStamp))
    }
    return ""
  }

  fun getDate(milliSeconds: Long, dateFormat: String): String {
    val formatter = SimpleDateFormat(dateFormat)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.time)
  }

  fun parseDate(time: String?, format: String?, required: String?, locale: Locale = Locale.getDefault(), timeZone: TimeZone? = TimeZone.getDefault()): String? {
    try {
      val timeFormat: DateFormat = SimpleDateFormat(format, locale)
      timeZone?.let { timeFormat.timeZone = it }
      val date = timeFormat.parse(time)
      return SimpleDateFormat(required, locale).format(date)
    } catch (e: Exception) {
      Log.d("parseDate", e.localizedMessage ?: "")
    }
    return ""
  }

  fun Date.parseDate(format: String, locale: Locale = Locale.getDefault(), timeZone: TimeZone? = TimeZone.getDefault()): String? {
    val timeFormat: DateFormat = SimpleDateFormat(format, locale)
    timeZone?.let { timeFormat.timeZone = it }
    return timeFormat.format(this)
  }

  fun String.parseDate(format: String, locale: Locale = Locale.getDefault(), timeZone: TimeZone? = TimeZone.getDefault()): Date? {
    return try {
      val timeFormat: DateFormat = SimpleDateFormat(format, locale)
      timeZone?.let { timeFormat.timeZone = it }
      timeFormat.parse(this)
    } catch (e: Exception) {
      Log.d("parseDate", e.localizedMessage ?: "")
      null
    }
  }

  fun isBetweenValidTime(startTime: Date, endTime: Date, validateTime: Date): Boolean {
    var validTimeFlag = false
    if (endTime <= startTime) {
      if (validateTime <= endTime || validateTime >= startTime) validTimeFlag = true
    } else if (validateTime in startTime..endTime) validTimeFlag = true
    return validTimeFlag
  }

  fun isStartEndDatesValid(startTime: Date, endTime: Date): Boolean{
    return startTime < endTime
  }

  fun getCurrentDate(): Date {
    return Calendar.getInstance().time
  }

  fun getAmountDate(amount: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, amount)
    return cal.time
  }

  fun getAmountYearDate(amount: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.YEAR, amount)
    return cal.time
  }

  fun Date.getAmountMinDate(amount: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MINUTE, amount)
    return cal.time
  }

  fun Date.toCalendar(): Calendar? {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
  }

  fun getCurrentTimeIn24Hour(): String {
    val now = Calendar.getInstance()
    return "${now[Calendar.HOUR_OF_DAY]}:${now[Calendar.MINUTE]}"
  }

  fun formatDate(tstSeconds: Long): String {
    return formatDate(Date(TimeUnit.SECONDS.toMillis(tstSeconds)))
  }

  fun formatDateShort(tstSeconds: Long): String {
    val d = Date(TimeUnit.SECONDS.toMillis(tstSeconds))
    return if (DateUtils.isToday(d.time)) {
      dateFormaterToday.format(d)
    } else {
      dateFormaterDate.format(d)
    }
  }

  fun formatDate(d: Date): String {
    return if (DateUtils.isToday(d.time)) {
      dateFormaterToday.format(d)
    } else {
      dateFormater.format(d)
    }
  }

  fun getDateMillSecond(date: String): Long {
    var date = date
    var dateTime: Array<String>? = null
    var dateMilliseconds: Long = 0
    if (date.contains("/Date")) {
      date = date.replace("/Date(", "").replace(")/", "")
    }
    if (date.contains("+")) {
      dateTime = date.split("\\+".toRegex()).toTypedArray()
      if (dateTime[1].length > 1) {
        dateMilliseconds += dateTime[1].substring(0, 2).toInt() * 60 * 60 * 1000.toLong()
      }
      if (dateTime[1].length > 3) {
        dateMilliseconds += dateTime[1].substring(2, 4).toInt() * 60 * 1000.toLong()
      }
      dateMilliseconds += java.lang.Long.valueOf(dateTime[0])
    } else {
      dateMilliseconds += java.lang.Long.valueOf(date)
    }
    return dateMilliseconds
  }

  fun millisecondsToMinutesSeconds(milliSeconds: Long): String? {
    return String.format(
      "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliSeconds).mod(TimeUnit.HOURS.toMinutes(1)),
      TimeUnit.MILLISECONDS.toSeconds(milliSeconds).mod(TimeUnit.MINUTES.toSeconds(1))
    )
  }

  fun milliToMinSecFormat(milliSeconds: Long): String? {
    return String.format(
      "%d min %d sec", TimeUnit.MILLISECONDS.toMinutes(milliSeconds).mod(TimeUnit.HOURS.toMinutes(1)),
      TimeUnit.MILLISECONDS.toSeconds(milliSeconds).mod(TimeUnit.MINUTES.toSeconds(1))
    )
  }
}

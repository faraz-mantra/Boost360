package com.framework.utils

import android.text.TextUtils
import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object DateUtils {

  const val FORMAT_SERVER_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  const val FORMAT_SERVER_TO_LOCAL = "dd/MM/yyyy HH:mm a"
  const val FORMAT_DD_MM_YYYY_hh_mm_ss = "dd-MM-yyyy hh:mm:ss"
  const val FORMAT__DD__MM__YYYY = "dd MM yyyy"
  const val FORMAT_DD_MM_YYYY = "dd-MM-yyyy"
  const val FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
  const val FORMAT_HH_MM_SS_A = "hh:mm:ss a"
  const val FORMAT_HH_MM_SS = "HH:mm:ss"
  const val FORMAT_HH_MM = "HH:mm a"
  const val FORMAT_H_MM_A = "h:mm a"
  private val dateFormater = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
  private val dateFormaterToday = SimpleDateFormat("HH:mm", Locale.getDefault())
  private val dateFormaterDate = SimpleDateFormat("HH:mm", Locale.getDefault())

  fun parseDate(timestamp: String, format: String?): String {
    if (!TextUtils.isEmpty(timestamp) && !TextUtils.isEmpty(format)) {
      val timeStamp = timestamp.toLong()
      val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
      return dateFormat.format(Date(timeStamp))
    }
    return ""
  }

  fun parseDate(time: String?, format: String?, required: String?): String {
    try {
      val timeFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
      val date = timeFormat.parse(time)
      return SimpleDateFormat(required, Locale.getDefault()).format(date)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return ""
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
}

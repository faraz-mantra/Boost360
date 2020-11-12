package com.inventoryorder.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.floor

const val ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
var UTC: TimeZone? = null


inline fun <reified T : Any> Any.cast(): T? {
  return this as? T
}

inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A?, B?>? {
  if (first !is A || second !is B) return null
  return first as? A to second as? B
}
/* TODO Implementation asPairOf
   val somePair: Pair<Any?, Any?> = "items" to listOf(1, 2, 3)
   val stringToSomething = somePair.asPairOf<String, Any>()
*/

fun convertMinutesToDays(minutes: Double): String {
  val onedayMinutes = 1440
  val restMinutes: Int?
  val hours: Int?
  return if (minutes < 60) {
    minutes.takeIf { it > 1 }?.let { "${minutes.toInt()} Minutes" } ?: "${minutes.toInt()} Minute"
  } else if (minutes > 60 && minutes < onedayMinutes) {
    hours = floor(minutes / 60).toInt()
    restMinutes = (minutes % 60).toInt()
    val h = hours.takeIf { it > 1 }?.let { "$hours Hours" } ?: "$hours Hour"
    val m = restMinutes.takeIf { it > 1 }?.let { "$restMinutes Minutes" } ?: "$restMinutes Minute"
    "$h $m"
  } else {
    val days = floor((minutes / 60) / 24).toInt()
    val min = (minutes % onedayMinutes)
    hours = floor(min / 60).toInt()
    restMinutes = (min % 60).toInt()
    val d = days.takeIf { it > 1 }?.let { "$days Days" } ?: "$days Day"
    val h = hours.takeIf { it > 1 }?.let { "$hours Hours" } ?: "$hours Hour"
    val m = restMinutes.takeIf { it > 1 }?.let { "$restMinutes Minutes" } ?: "$restMinutes Minute"
    "$d $h $m"
  }
}

fun Context.openWebPage(url: String): Boolean {
  return try {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    true
  } catch (e: Exception) {
    false
  }
}

fun Context.copyClipBoard(selectedText: String): Boolean {
  var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
  var clip = ClipData.newPlainText("label", selectedText)
  if (clipboard == null || clip == null)
    return false
  clipboard.setPrimaryClip(clip)
  return true
}

fun String.capitalizeUtil(): String {
  val capBuffer = StringBuffer()
  val capMatcher: Matcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(this)
  while (capMatcher.find()) {
    capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase(Locale.getDefault()) + capMatcher.group(2).toLowerCase(Locale.getDefault()))
  }
  return capMatcher.appendTail(capBuffer).toString()
}

fun getFormattedDate(date: String?): String? {
  var dateN = date!!
  var formatted = ""
  var dateTime = ""
  if (TextUtils.isEmpty(dateN)) return ""
  if (dateN.contains("/Date")) dateN = dateN.replace("/Date(", "").replace(")/", "")
  var epochTime: Long? = null
  try {
    epochTime = dateN.toLong()
  } catch (e: java.lang.Exception) {
    e.printStackTrace()
    UTC = TimeZone.getTimeZone("UTC")
    val sdf = SimpleDateFormat(ISO_8601_24H_FULL_FORMAT)
    sdf.timeZone = UTC
    try {
      epochTime = sdf.parse(dateN).time
    } catch (parseException: ParseException) {
      parseException.printStackTrace()
    }
  }
  val date = Date(epochTime!!)
  val format: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH) //dd/MM/yyyy HH:mm:ss
  format.timeZone = TimeZone.getDefault()
  dateTime = format.format(date)
  if (dateTime.isNullOrEmpty().not()) {
    val hrsTemp: String
    val amMarker: String
    var dateTemp: Array<String> = dateTime.split(" ".toRegex()).toTypedArray()
    hrsTemp = dateTemp[1]
    amMarker = dateTemp[2]
    dateTemp = dateTemp[0].split("-".toRegex()).toTypedArray()
    if (dateTemp.isNotEmpty()) {
      when (dateTemp[1].toInt()) {
        1 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Jan, " + dateTemp[2]
        }
        2 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Feb, " + dateTemp[2]
        }
        3 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Mar, " + dateTemp[2]
        }
        4 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Apr, " + dateTemp[2]
        }
        5 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " May, " + dateTemp[2]
        }
        6 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " June, " + dateTemp[2]
        }
        7 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " July, " + dateTemp[2]
        }
        8 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Aug, " + dateTemp[2]
        }
        9 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Sept, " + dateTemp[2]
        }
        10 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Oct, " + dateTemp[2]
        }
        11 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Nov, " + dateTemp[2]
        }
        12 -> {
          dateTemp[0] = AddSuffixForDay(dateTemp[0])
          formatted = dateTemp[0] + " Dec, " + dateTemp[2]
        }
      }
    }
    formatted += " at $hrsTemp $amMarker"
  }
  return formatted
}

fun AddSuffixForDay(originalDay: String): String {
  var originalDayN = originalDay
  var day = ""
  if (originalDayN.startsWith("0")) originalDayN = originalDayN.replace("0", "")
  day = originalDayN + ""
  return day
}
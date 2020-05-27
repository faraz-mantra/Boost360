package com.inventoryorder.utils

import kotlin.math.floor

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
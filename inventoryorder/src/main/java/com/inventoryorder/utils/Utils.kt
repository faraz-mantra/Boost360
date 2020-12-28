package com.inventoryorder.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
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
package com.dashboard.utils

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.SystemClock
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.framework.glide.util.loadGifGlide
import com.framework.views.customViews.CustomImageView
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.floor

fun <F : Fragment> AppCompatActivity.getFragment(fragmentClass: Class<F>): F? {
  val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
  navHostFragment.childFragmentManager.fragments.forEach {
    if (fragmentClass.isAssignableFrom(it.javaClass)) {
      return it as F
    }
  }
  return null
}

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
  val clipboard: ClipboardManager? =
    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
  val clip = ClipData.newPlainText("label", selectedText)
  if (clipboard == null || clip == null) return false
  clipboard.setPrimaryClip(clip)
  return true
}

fun String.capitalizeUtil(): String {
  val capBuffer = StringBuffer()
  val capMatcher: Matcher =
    Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(this)
  while (capMatcher.find()) {
    capMatcher.appendReplacement(capBuffer,
      capMatcher.group(1).toUpperCase(Locale.getDefault()) + capMatcher.group(2)
        .toLowerCase(Locale.getDefault())
    )
  }
  return capMatcher.appendTail(capBuffer).toString()
}

fun File.getBitmap(): Bitmap? {
  return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), 400, 400)
}

fun String.getBitmap(): Bitmap? {
  return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this), 400, 400)
}

fun File.getMimeType(): String? {
  var mimeType: String? = null
  val extension: String? = absolutePath.getExtension()
  if (MimeTypeMap.getSingleton().hasExtension(extension)) {
    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
  }
  return mimeType
}

fun String.getExtension(): String? {
  val strLength = lastIndexOf(".")
  return if (strLength > 0) substring(strLength + 1).toLowerCase() else null
}

fun String.getFileName(): String? {
  return substring(lastIndexOf("/") + 1)
}

fun getExtensionUrl(url: String?): String {
  val filenameArray = url?.split("\\.".toRegex())?.toTypedArray()
  return filenameArray?.get(filenameArray.size - 1) ?: ""
}

fun Context.setGifAnim(image: CustomImageView, icGif: Int, placeHolder: Int) {
  this.loadGifGlide(image, icGif, placeHolder)
}

const val INITIAL_POSITION = 0.0f
const val ROTATED_POSITION = 180f

fun CustomImageView.rotateImage(isExpanded: Boolean) {
  val startAngle = if (isExpanded) 0f else 180f
  ObjectAnimator.ofFloat(this, View.ROTATION, startAngle, startAngle + 180f).apply {
    duration = 200
    interpolator = LinearInterpolator()
    start()
  }
}


fun NestedScrollView.scrollToTopBottom(image: CustomImageView) {
  val lastChild = getChildAt(childCount - 1)
  val bottom = lastChild.bottom + paddingBottom
  val delta = bottom - (scrollY + height)
  if (delta == 0) {
    smoothScrollTo(0, 0, 1000)
    image.rotation = 90F
  } else {
    smoothScrollTo(0, getChildAt(0).height, 1000)
    image.rotation = -90F
  }
}





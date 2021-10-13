package com.framework.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.framework.BaseApplication
import com.framework.views.customViews.CustomTextView
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

fun View.setNoDoubleClickListener(listener: View.OnClickListener, blockInMillis: Long = 1000) {
  var lastClickTime: Long = 0
  this.setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClickTime < blockInMillis) return@setOnClickListener
    lastClickTime = SystemClock.elapsedRealtime()
    listener.onClick(this)
  }
}

fun Double.roundToFloat(numFractionDigits: Int):Float = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toFloat()

fun Activity.hideKeyBoard() {
  val view = this.currentFocus
  if (view != null) {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }
}

fun Activity.showKeyBoard(view: View?) {
  view?.post {
    view.requestFocus()
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
  }
}

fun hasHTMLTags(text: String): Boolean {
  val matcher: Matcher = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>").matcher(text)
  return matcher.find()
}

fun fromHtml(html: String?): Spanned? {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
    html,
    Html.FROM_HTML_MODE_LEGACY
  )
  else Html.fromHtml(html)
}

fun getNumberFormat(value: String): String {
  return try {
    NumberFormat.getNumberInstance(Locale.US).format(value.toInt().toLong())
  } catch (e: Exception) {
    value
  }
}

fun Double.roundTo(n: Int): Double {
  return "%.${n}f".format(this).toDouble()
}

fun CustomTextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
  val spannableString = SpannableString(this.text)
  var startIndexOfLink = -1
  for (link in links) {
    val clickableSpan = object : ClickableSpan() {
      override fun updateDrawState(textPaint: TextPaint) {
        // use this to change the link color
        textPaint.color = textPaint.linkColor
        // toggle below value to enable/disable
        // the underline shown below the clickable text
        textPaint.isUnderlineText = true
      }

      override fun onClick(view: View) {
        Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
        view.invalidate()
        link.second.onClick(view)
      }
    }
    startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
    spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
  this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun AppCompatActivity.getStatusBarHeight(): Int {
  var result = 0
  val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}

fun AppCompatActivity.getNavigationBarHeight(): Int {
  val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
  val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
  return if (resourceId > 0 && !hasMenuKey) {
    resources.getDimensionPixelSize(resourceId)
  } else 0
}

fun LottieAnimationView.changeLayersColor(@ColorRes colorRes: Int) {
  val color = ContextCompat.getColor(context, colorRes)
  val filter = SimpleColorFilter(color)
  val keyPath = KeyPath("**")
  val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
  addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024

fun <T> List<T>.toArrayList(): ArrayList<T> {
  return ArrayList(this)
}

fun View.toBitmap(): Bitmap? {
  val returnedBitmap =
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(returnedBitmap)
  val bgDrawable = background
  bgDrawable?.draw(canvas)

  draw(canvas)
  return returnedBitmap
}

fun Bitmap.shareAsImage(packageName:String?=null,text: String?=null){
    val imagesFolder = File(BaseApplication.instance.getExternalFilesDir(null), "shared_images")
    var uri: Uri? = null
    try {
      imagesFolder.mkdirs()
      val file = File(imagesFolder, "shareimage.png")
      val stream = FileOutputStream(file)
      compress(Bitmap.CompressFormat.PNG, 90, stream)
      stream.flush()
      stream.close()
      uri = FileProvider.getUriForFile(BaseApplication.instance, "${BaseApplication.instance.packageName}.provider", file)
      val intent = Intent(Intent.ACTION_SEND)
      intent.putExtra(Intent.EXTRA_STREAM, uri)
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.type = "image/png"
      packageName?.let {
        intent.`package`= packageName
      }
      text?.let {
        intent.putExtra(Intent.EXTRA_TEXT,text)
      }
      BaseApplication.instance.startActivity(intent)
    } catch (e: Exception) {
      Log.d("IOException: " , e.message.toString())
    }

}
fun Bitmap.saveAsImageToAppFolder(destPath:String): File? {
  var uri: Uri? = null
  val file = File(destPath)

  try {

    file.createNewFile()
    val stream = FileOutputStream(file)
    compress(Bitmap.CompressFormat.PNG, 90, stream)
    stream.flush()
    stream.close()
    return file
  }catch (e: IOException) {
    Log.d("IOException: " , e.message.toString())
    file.delete()
    return null
  }


}

fun Bitmap.saveImageToSharedStorage(
  filename: String=System.currentTimeMillis().toString()+".jpg",
) {
  val mimeType: String =  "image/jpeg"
  val directory: String = Environment.DIRECTORY_PICTURES
  val mediaContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
  val imageOutStream: OutputStream
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    val values = ContentValues().apply {
      put(MediaStore.Images.Media.DISPLAY_NAME, filename)
      put(MediaStore.Images.Media.MIME_TYPE, mimeType)
      put(MediaStore.Images.Media.RELATIVE_PATH, directory)
    }

    BaseApplication.instance.contentResolver.run {
      val uri =
        BaseApplication.instance.contentResolver.insert(mediaContentUri, values)
          ?: return
      imageOutStream = openOutputStream(uri) ?: return
    }
  } else {
    val imagePath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
    val image = File(imagePath, filename)
    imageOutStream = FileOutputStream(image)
  }

  imageOutStream.use {compress(Bitmap.CompressFormat.JPEG, 100, it) }
}
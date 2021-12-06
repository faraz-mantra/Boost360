package com.framework.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.framework.BaseApplication
import com.framework.constants.PackageNames
import com.framework.views.customViews.CustomTextView
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
private const val TAG = "Util"
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

fun AppCompatTextView.setIconifiedText(text: String, @DrawableRes iconResId: Int? = null, textColor: String? = null, @ColorRes color: Int? = null, textFont: String? = null, @FontRes font: Int? = null) {
  SpannableStringBuilder("$text#").apply {
    if (textColor.isNullOrEmpty().not() && color != null) {
      val colorRes = ContextCompat.getColor(this@setIconifiedText.context, color)
      val p1 = Pattern.compile(textColor, Pattern.CASE_INSENSITIVE)
      val m1 = p1.matcher(text)
      while (m1.find()) {
        this.setSpan(ForegroundColorSpan(colorRes), m1.start(), m1.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
      }
    }
    if (textFont.isNullOrEmpty().not() && font != null) {
      val style = ResourcesCompat.getFont(this@setIconifiedText.context, font)?.style ?: Typeface.BOLD
      val p2 = Pattern.compile(textFont, Pattern.LITERAL)
      val m2 = p2.matcher(text)
      while (m2.find()) {
        this.setSpan(StyleSpan(style), m2.start(), m2.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
    }
    if (iconResId != null) {
      val align = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) DynamicDrawableSpan.ALIGN_CENTER else DynamicDrawableSpan.ALIGN_BASELINE
      setSpan(ImageSpan(context, iconResId, align), text.length, text.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
  }.let { setText(it) }
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
//      if(startIndexOfLink == -1) continue //if you want to verify your texts contains links text
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

suspend fun Bitmap.shareAsImage(packageName:String?=null,text: String?=null){
    val imagesFolder = File(BaseApplication.instance.getExternalFilesDir(null), "shared_images")
    var uri: Uri? = null
    try {
      imagesFolder.mkdirs()
      val file = File(imagesFolder, "shareimage${System.currentTimeMillis()}.jpg")
      val stream = FileOutputStream(file)
      compress(Bitmap.CompressFormat.JPEG, 100, stream)
      stream.flush()
      stream.close()
      uri = FileProvider.getUriForFile(BaseApplication.instance, "${BaseApplication.instance.packageName}.provider", file)
      val intent = Intent(Intent.ACTION_SEND)
      intent.putExtra(Intent.EXTRA_STREAM, uri)
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.type = "image/*"
      packageName?.let {
        intent.`package`= packageName
      }
      text?.let {
        intent.putExtra(Intent.EXTRA_TEXT,text)
      }
      BaseApplication.instance.startActivity(intent)
    } catch (e: Exception) {
      Log.d("IOException: " , e.message.toString())
      if (e is ActivityNotFoundException){
        withContext(Dispatchers.Main){
          when(packageName){
            PackageNames.WHATSAPP->{
              Toast.makeText(BaseApplication.instance,"Whatsapp is not installed on your device",Toast.LENGTH_LONG).show()

            }
            PackageNames.INSTAGRAM->{
              Toast.makeText(BaseApplication.instance,"Instagram is not installed on your device",Toast.LENGTH_LONG).show()

            }
          }
        }

      }
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



suspend fun Bitmap.saveImageToStorage(

  filename: String=System.currentTimeMillis().toString()+".jpg",
  showNoti:Boolean=false
  ){
  val noti_id = System.currentTimeMillis().toInt()

  try {
    val notificationBuilder = NotiUtils.showNoti("Downloading Image", 0)

    if (showNoti) {
      NotiUtils.notificationManager?.notify(noti_id, notificationBuilder.build())
    }
    var fileUri: Uri? = null

    val mimeType: String = "image/jpeg"
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
        fileUri = uri
        imageOutStream = openOutputStream(uri) ?: return
      }
    } else {
      val imagePath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
      val image = File(imagePath, filename)
      imageOutStream = FileOutputStream(image)
      fileUri = Uri.fromFile(image)
    }


    val byteOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream)
    val mbitmapdata: ByteArray = byteOutputStream.toByteArray()
    val inputStream = ByteArrayInputStream(mbitmapdata)
    val buffer = ByteArray(128) //Use 1024 for better performance
    val lenghtOfFile = mbitmapdata.size
    var totalWritten = 0
    var bufferedBytes = 0
    var progress = 0
    while (inputStream.read(buffer).also { bufferedBytes = it } > 0) {
      totalWritten += bufferedBytes
      progress = (totalWritten * 100 / lenghtOfFile)
      Log.i(TAG, "saveImageToStorage: progress $progress")
      if (showNoti) {
        notificationBuilder.setProgress(100, progress, false)
        NotiUtils.notificationManager?.notify(noti_id, notificationBuilder.build())
      }
      imageOutStream.write(buffer, 0, bufferedBytes)
    }
    if (showNoti) {
      if (progress >= 100) {
        Log.i(TAG, "saveImageToStorage: success")
        notificationBuilder.setContentTitle("Image Downloaded")
          ?.setProgress(0, 0, false)
          .setContentIntent(getFileViewerIntent(fileUri, mimeType).getPendingIntent())
        NotiUtils.notificationManager?.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
      } else {
        Toast.makeText(BaseApplication.instance, "Failed To Save Image", Toast.LENGTH_SHORT).show()
      }
      NotiUtils.notificationManager?.cancel(noti_id)
    }


  } catch (e: IOException) {
    Toast.makeText(BaseApplication.instance, "Failed To Save Image", Toast.LENGTH_SHORT).show()
    NotiUtils.notificationManager?.cancel(noti_id)

    e.printStackTrace()
  }
}

fun getFileViewerIntent(uri: Uri?,type:String): Intent {
  val newIntent = Intent(Intent.ACTION_VIEW)
  newIntent.setDataAndType(uri, type)
  newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
 return newIntent
}

fun Intent.getPendingIntent(): PendingIntent? {
  return PendingIntent.getActivity(
    BaseApplication.instance,
    System.currentTimeMillis().toInt(), this, PendingIntent.FLAG_UPDATE_CURRENT
  )
}

fun isService(category_code: String?): Boolean {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> true
    else -> false
  }
}

inline fun <reified T> read(): T {
  val value: String = readLine()!!
  return when (T::class) {
    Int::class -> value.toInt() as T
    String::class -> value as T
    // add other types here if need
    else -> throw IllegalStateException("Unknown Generic Type")
  }
}

fun Activity.makeCall(number: String) {
  val callIntent = Intent(Intent.ACTION_DIAL)
  callIntent.addCategory(Intent.CATEGORY_DEFAULT)
  callIntent.data = Uri.parse("tel:$number")
  this.startActivity(Intent.createChooser(callIntent, "Call by:"))
}

fun getAppVersionName(): String? {
  try {
    val pInfo: PackageInfo = BaseApplication.instance.packageManager.getPackageInfo(BaseApplication.instance.packageName, 0)
    val version = pInfo.versionName
    return version
  } catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
  }
  return null
}
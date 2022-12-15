package com.framework.utils

import android.app.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.*
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Typeface
import android.graphics.*
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.framework.BaseApplication
import com.framework.R
import com.framework.analytics.SentryController
import com.framework.constants.PackageNames
import com.framework.pref.APPLICATION_JIO_ID
import com.framework.views.customViews.CustomTextView
import com.google.android.material.snackbar.Snackbar
import com.google.common.io.ByteStreams.readBytes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


private const val TAG = "Util"

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

fun View.setNoDoubleClickListener(listener: View.OnClickListener, blockInMillis: Long = 1000) {
  var lastClickTime: Long = 0
  this.setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClickTime < blockInMillis) return@setOnClickListener
    lastClickTime = SystemClock.elapsedRealtime()
    listener.onClick(this)
  }
}

fun Double.roundToFloat(numFractionDigits: Int): Float = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toFloat()

fun AppCompatEditText.onDone(callback: () -> Unit) {
  imeOptions = EditorInfo.IME_ACTION_DONE
  maxLines = 1
  setOnEditorActionListener { _, actionId, _ ->
    if (actionId == EditorInfo.IME_ACTION_DONE) {
      callback.invoke()
      true
    }
    false
  }
}

@SuppressLint("ClickableViewAccessibility")
fun AppCompatEditText.onRightDrawableClicked(onClicked: (view: AppCompatEditText) -> Unit) {
  this.setOnTouchListener { v, event ->
    var hasConsumed = false
    if (v is AppCompatEditText) {
      if (event.x >= v.width - v.totalPaddingRight) {
        if (event.action == MotionEvent.ACTION_UP) {
          onClicked(this)
        }
        hasConsumed = true
      }
    }
    hasConsumed
  }
}
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
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
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


fun makeSectionOfTextBold(text: String, textToBold: String, @ColorRes color: Int = android.R.color.black, @FontRes font: Int = R.font.semi_bold): SpannableStringBuilder? {
  val builder = SpannableStringBuilder()
  if (textToBold.isNotEmpty() && textToBold.trim { it <= ' ' } != "") {

    //for counting start/end indexes
    val testText = text.lowercase(Locale.US)
    val testTextToBold = textToBold.lowercase(Locale.US)
    val startingIndex = testText.indexOf(testTextToBold)
    val endingIndex = startingIndex + testTextToBold.length
    //for counting start/end indexes
    if (startingIndex < 0 || endingIndex < 0) {
      return builder.append(text)
    } else if (startingIndex >= 0 && endingIndex >= 0) {
      builder.append(text)
      val font = ResourcesCompat.getFont(BaseApplication.instance, font)?.style ?: Typeface.BOLD
      val color = ContextCompat.getColor(BaseApplication.instance, color)
      builder.setSpan(StyleSpan(font), startingIndex, endingIndex, 0)
      builder.setSpan(ForegroundColorSpan(color), startingIndex, endingIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
  } else {
    return builder.append(text)
  }
  return builder
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

fun Double.removeENotationAndRoundTo(n: Int): Double {
  return "%.${n}f".format(this.toString().replace("E", "").toDouble()).toDouble()
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
//      if(startIndexOfLink == -1) continue //TODO if you want to verify your texts contains links text
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
              //Toast.makeText(BaseApplication.instance,"Whatsapp is not installed on your device",Toast.LENGTH_LONG).show()
              shareAsImage(
                PackageNames.WHATSAPP_BUSINESS,
                text = text
              )
            }
            PackageNames.WHATSAPP_BUSINESS->{
              Toast.makeText(BaseApplication.instance,"Whatsapp is not installed on your device",Toast.LENGTH_LONG).show()
            }
            PackageNames.INSTAGRAM->{
              Toast.makeText(BaseApplication.instance,"Instagram is not installed on your device",Toast.LENGTH_LONG).show()

            }
          }
        }

      }else{
        SentryController.captureException(e)
      }
    }

}

fun Bitmap.saveAsImageToAppFolder(destPath: String): File? {
  var uri: Uri? = null
  val file = File(destPath)

  try {

    file.createNewFile()
    val stream = FileOutputStream(file)
    compress(Bitmap.CompressFormat.PNG, 90, stream)
    stream.flush()
    stream.close()
    return file
  } catch (e: IOException) {
    Log.d("IOException: ", e.message.toString())
    file.delete()
    return null
  }


}

fun Bitmap.saveAsTempFile(): File? {
 return saveAsImageToAppFolder(
    BaseApplication.instance.externalCacheDir?.toString()+File.separator+"tempimage.png")

}


suspend fun Bitmap.saveImageToStorage(

  filename: String = System.currentTimeMillis().toString() + ".jpg",
  showNoti: Boolean = false
) {
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
        notificationBuilder?.setProgress(100, progress, false)
        NotiUtils.notificationManager?.notify(noti_id, notificationBuilder.build())
      }
      imageOutStream.write(buffer, 0, bufferedBytes)
    }
    if (showNoti) {
      if (progress >= 100) {
        Log.i(TAG, "saveImageToStorage: success")
        notificationBuilder?.setContentTitle("Image Downloaded")
          ?.setProgress(0, 0, false)
          ?.setContentIntent(getFileViewerIntent(fileUri, mimeType).getPendingIntent())
        NotiUtils.notificationManager?.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
      } else {
        Toast.makeText(BaseApplication.instance, "Failed To Save Image", Toast.LENGTH_SHORT).show()
      }
      NotiUtils.notificationManager?.cancel(noti_id)
    }


  } catch (e: IOException) {
    Toast.makeText(BaseApplication.instance, "Failed To Save Image", Toast.LENGTH_SHORT).show()
    NotiUtils.notificationManager?.cancel(noti_id)
    SentryController.captureException(e)
    e.printStackTrace()
  }
}

fun getFileViewerIntent(uri: Uri?, type: String): Intent {
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

fun makeCall(number: String?) {
  val callIntent = Intent(Intent.ACTION_DIAL)
  callIntent.addCategory(Intent.CATEGORY_DEFAULT)
  callIntent.data = Uri.parse("tel:$number")
  callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  BaseApplication.instance.startActivity(callIntent)
}

fun Activity.makeCall(number: String) {
  val callIntent = Intent(Intent.ACTION_DIAL)
  callIntent.addCategory(Intent.CATEGORY_DEFAULT)
  callIntent.data = Uri.parse("tel:$number")
  callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
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

@RequiresApi(Build.VERSION_CODES.Q)
fun Drawable.setColorFilterApiQ(color: Int, blendMode:BlendMode){
    colorFilter = BlendModeColorFilter(color, blendMode)
}



fun highlightHashTag(text: String?,@ColorRes colorId: Int,@FontRes fontId:Int): SpannableString {

  val spannable = SpannableString(text?:"")

  if (text.isNullOrEmpty().not()){
    var last_index = 0
    text?.trim()?.split(Regex("\\s+"))?.forEach {
      Log.i(TAG, "addHashTagFunction: $it")
      if (it.isNotEmpty() && it[0] == '#'){

        spannable.setSpan(object : TypefaceSpan(null) {
          override fun updateDrawState(ds: TextPaint) {
            ds.typeface = Typeface.create(ResourcesCompat.getFont(BaseApplication.instance,
              fontId), Typeface.NORMAL) // To change according to your need
          }
        }, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) // To change according to your need

//        val boldSpan = StyleSpan(Typeface
//          .BOLD)
        val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(BaseApplication.instance, colorId))
        spannable.setSpan(foregroundSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannable.setSpan(boldSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

      }

      last_index+=it.length-1

    }
  }

  return spannable
}





inline fun <reified T> convertJsonToObj(json: String?) = Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)

fun Bitmap.zoom(percent: Float): Bitmap? {

  val scaleFactor = percent // Set this to the zoom factor
  val widthOffset = (scaleFactor / 2 * width).toInt()
  val heightOffset = (scaleFactor / 2 * height).toInt()
  val numWidthPixels: Int = width - 2 * widthOffset
  val numHeightPixels: Int = height - 2 * heightOffset
  return if (widthOffset > 0 && heightOffset > 0 && numHeightPixels > 0 && numWidthPixels > 0) {
    val rescaledBitmap = Bitmap.createBitmap(
      this, widthOffset, heightOffset, numWidthPixels, numHeightPixels, null, true
    )
    rescaledBitmap
  } else {
    this
  }
}

fun gcd(num1: Int, num2: Int): Int {
  var gcd = 1

  var i = 1
  while (i <= num1 && i <= num2) {
    // Checks if i is factor of both integers
    if (num1 % i == 0 && num2 % i == 0)
      gcd = i
    ++i
  }
  return gcd
}

fun spanBold(fullText: String, vararg boldTextList: String): SpannableString {
  val spannable = SpannableString(fullText)
  boldTextList.forEach { boldText ->
    spannable.setSpan(StyleSpan(Typeface.BOLD), fullText.indexOf(boldText), fullText.indexOf(boldText) + boldText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  return spannable
}

fun sendNotification(
  c: Context,
  messageTitle: String?,
  messageBody: String,
  deepLinkUrl: String
) {
  val intent = Intent("com.dashboard.controller.DashboardActivity")
  val stackBuilder = TaskStackBuilder.create(c)
  intent.putExtra("from", "notification")
  intent.putExtra("url", deepLinkUrl)
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  stackBuilder.addNextIntentWithParentStack(intent)
  val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
  val notificationBuilder: NotificationCompat.Builder =
    NotificationCompat.Builder(c, "111")
      .setSmallIcon(R.drawable.app_launcher2)
      .setLargeIcon(
        BitmapFactory.decodeResource(
          c.resources,
          R.drawable.app_launcher
        )
      )
      .setContentText(messageBody)
      .setAutoCancel(false)
      .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
      .setColor(ContextCompat.getColor(c, R.color.colorPrimary))
      .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//        .setContentIntent(pendingIntent)
      .addAction(R.drawable.app_launcher, "Renew now", pendingIntent)
      .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setLights(Color.GREEN, 3000, 3000)
  if (messageTitle != null && messageTitle.isNotEmpty())
    notificationBuilder.setContentTitle(messageTitle)
  else
    notificationBuilder.setContentTitle(c.resources.getString(R.string.app_name))
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val notificationManager =
      c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    val importance = NotificationManager.IMPORTANCE_HIGH
    val notificationChannel =
      NotificationChannel("111", "NOTIFICATION_CHANNEL_NAME", importance)
    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.YELLOW
    notificationChannel.enableVibration(true)
    notificationChannel.setShowBadge(false)
    notificationChannel.vibrationPattern =
      longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
    assert(notificationManager != null)
    notificationBuilder.setChannelId("111")
    notificationManager!!.createNotificationChannel(notificationChannel)
    notificationManager.notify(0, notificationBuilder.build())
  } else {
    val notificationManager = NotificationManagerCompat.from(c)
    notificationManager.notify(0, notificationBuilder.build())
  }
}

fun spanColor(fullText:String,@ColorRes color: Int,vararg colorTextList:String): SpannableString {
  val spannable = SpannableString(fullText)
  colorTextList.forEach { text->
    spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(
      BaseApplication.instance,color
    )),fullText.indexOf(text),fullText.indexOf(text)+text.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  return spannable
}

fun spanBoldNdColor(fullText:String,@ColorRes color: Int,text:String): SpannableString {
  val spannable = SpannableString(fullText)
  spannable.setSpan(StyleSpan(Typeface.BOLD),fullText.indexOf(text),fullText.indexOf(text)+text.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

  spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(
    BaseApplication.instance,color
  )),fullText.indexOf(text),fullText.indexOf(text)+text.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

  return spannable
}

fun spanClick(fullText:String,function: () -> (Unit),vararg colorTextList:String): SpannableString {
  val spannable = SpannableString(fullText)
  colorTextList.forEach { text->
    spannable.setSpan(object :ClickableSpan(){
      override fun onClick(p0: View) {
        function.invoke()
      }

    },fullText.indexOf(text),fullText.indexOf(text)+text.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  return spannable
}


fun File.shareAsImage(context:Context,packageName: String?,text: String?){
  val uri= FileProvider.getUriForFile(
    context,
    "${context.packageName}.provider", //(use your app signature + ".provider" )
    this)
  val intent = Intent(Intent.ACTION_SEND)
  intent.type = "image/*"
  intent.putExtra(Intent.EXTRA_STREAM, uri)
  intent.putExtra(Intent.EXTRA_TEXT,text)
  if (packageName!=null){
    intent.setPackage(packageName)
  }
  context.startActivity(intent)
}

fun ImageView.loadFromFile(imgFile:String?,cache:Boolean=true){
  val builder = Glide.with(this).load(imgFile)
  if (cache.not()){
    builder.apply(RequestOptions.skipMemoryCacheOf(true))
      .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(this)
  }else{
    builder.into(this)

  }
}

fun ImageView.loadFromUrl(imgUrl:String?,cache:Boolean=true){
  val builder = Glide.with(this).load(imgUrl)
  if (cache.not()){
    builder.apply(RequestOptions.skipMemoryCacheOf(true))
      .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(this)
  }else{
    builder.into(this)

  }
}

fun String.extractHashTag(): ArrayList<String> {
  val MY_PATTERN = Pattern.compile("#(\\S+)");
  val mat = MY_PATTERN.matcher(this);
  val strs= ArrayList<String>();
  while (mat.find()) {
    //System.out.println(mat.group(1));
    strs.add(mat.group(1));
  }
  return strs
}

fun Activity.setStatusBarColor(@ColorRes colorId: Int){
  val window = window
  window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
  window.statusBarColor = ContextCompat.getColor(this,colorId)
  WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isColorDark(colorId)


}

fun Fragment.setStatusBarColor(@ColorRes colorId: Int){
  activity?.setStatusBarColor(colorId)
}

fun isColorDark(@ColorRes colorRes: Int): Boolean {
  val color = ContextCompat.getColor(BaseApplication.instance,colorRes)
  val whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color)
  val blackContrast = ColorUtils.calculateContrast(Color.BLACK, color)

  return if (whiteContrast > blackContrast) false else true

}

fun TypedArray.use(block: TypedArray.() -> Unit) {
  try {
    block()
  } finally {
    this.recycle()
  }
}

fun Context.getStyledAttributes(@StyleableRes attrs: IntArray, block: TypedArray.() -> Unit) =
  this.obtainStyledAttributes(attrs).use(block)

fun View.setClickableRipple() {
  val attrs = intArrayOf(R.attr.selectableItemBackground)
  context.getStyledAttributes(attrs) {
    val backgroundResource = getResourceId(0, 0)
    setBackgroundResource(backgroundResource)
  }
}

fun File.toBase64(): String? {
  val result: String?
  inputStream().use { inputStream ->
    val sourceBytes = inputStream.readBytes()
    result = android.util.Base64.encodeToString(sourceBytes, android.util.Base64.DEFAULT)
  }

  return result
}

fun copyToClipBoard(text: String) {
  val clipboard = BaseApplication.instance.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
  val clip: ClipData = ClipData.newPlainText("boost-label", text)
  clipboard.setPrimaryClip(clip)
  Toast.makeText(BaseApplication.instance, BaseApplication.instance.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
}

fun Activity.checkPermission(permString: String): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    checkSelfPermission(permString) == PackageManager.PERMISSION_GRANTED
  } else {
    true
  }
}

fun Fragment.checkPermission(permString: String): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    ActivityCompat.checkSelfPermission(requireContext(), permString) == PackageManager.PERMISSION_GRANTED
  } else {
    true
  }
}

fun showToast(text: String?, dur: Int = Toast.LENGTH_LONG) {
  if (text != null && text.isNotEmpty()) Toast.makeText(BaseApplication.instance, text, dur).show()
}

suspend fun runOnUi(func: () -> Unit) {
  withContext(Dispatchers.Main) { func.invoke() }
}

fun fetchString(id: Int): String {
  return BaseApplication.instance.getString(id)
}

fun fetchColor(id: Int): Int {
  return ContextCompat.getColor(BaseApplication.instance,id)
}
fun showSnackBarNegative(context: Activity, msg: String?) {
  val snackBar = Snackbar.make(context.findViewById(android.R.id.content), msg ?: "", Snackbar.LENGTH_INDEFINITE)
  snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_negative_color))
  snackBar.duration = 4000
  snackBar.show()
}

fun String.capitalized(): String {
  return this.replaceFirstChar {
    if (it.isLowerCase())
      it.titlecase(Locale.getDefault())
    else it.toString()
  }
}

fun Uri.toBase64(): String? {
  return try {
    val bytes = BaseApplication.instance.contentResolver.openInputStream(this)?.readBytes()

    Base64.encodeToString(bytes,Base64.DEFAULT)
  } catch (error: IOException) {
    error.printStackTrace() // This exception always occurs
    null
  }
}

fun isJioBuild(): Boolean {
 return BaseApplication.instance.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)
}

fun application()=BaseApplication.instance

fun Bitmap.toBase64(): String {
  val byteStream = ByteArrayOutputStream()
  compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
  val byteArray = byteStream.toByteArray()
  val baseString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
  return baseString
}
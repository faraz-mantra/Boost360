package com.framework.utils

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.framework.BaseApplication
import com.framework.R
import com.framework.analytics.SentryController
import com.framework.base.BaseActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream

class ContentSharing {

  companion object {

    var targetMap: Target? = null
    fun shareProduct(
      name: String? = null, price: String? = null, link: String? = null, vmn: String? = null, imageUri: String? = null,
      isWhatsApp: Boolean? = false, isService: Boolean? = false, isFb: Boolean? = false, activity: Activity
    ) {
      val orderAppointment = if (isService == true) "appointment" else "order"
      val templateBuilder = StringBuilder()
      if (name.isNullOrBlank().not()) {
        templateBuilder.append("🆕 *Item name:* $name").append("\n")
      }
      if (price.isNullOrBlank().not()) {
        templateBuilder.append("🏷️ *Price:* Rs.$price").append("\n")
      }
      if (vmn.isNullOrBlank().not()) {
        templateBuilder.append("📞 Feel free to call $vmn if you need any help. ").append("\n")
      }
      if (orderAppointment.isBlank().not() && link.isNullOrBlank().not()) {
        templateBuilder.append("👉🏼 *Place your $orderAppointment here:* $link")
      }
      share(activity, shareText = templateBuilder.toString(), imageUri = imageUri, isWhatsApp = isWhatsApp, isFb = isFb)
    }

    fun shareUpdates(
      activity: Activity, updateContent: String, link: String?, catalogLink: String,
      vmn: String?, isWhatsApp: Boolean?, isFb: Boolean?, imageUri: String? = null
    ) {
      val templateBuilder = StringBuilder()
      if (updateContent.isBlank().not() && link.isNullOrBlank().not()) {
        templateBuilder.append("👋🏼 Hey there!")
        templateBuilder.append("${truncateString(updateContent, 100)}: Read more $link")
          .append("\n")
      }
      if (catalogLink.isBlank().not()) {
        templateBuilder.append("🏷️ Check our online catalogue, $catalogLink").append("\n")
      }
      if (vmn.isNullOrBlank().not()) {
        templateBuilder.append("📞 Feel free to call $vmn if you need any help. ")
      }
      share(activity = activity, templateBuilder.toString(), isWhatsApp = isWhatsApp, isFb = isFb, imageUri = imageUri)
    }

    fun truncateString(string: String, maxChar: Int): String {
      return if (string.length < maxChar) {
        string
      } else {
        string.substring(0, maxChar) + "..."
      }
    }

    fun shareCustomPages(
      activity: Activity, pageName: String? = "", link: String? = "", vmn: String? = "",
      catalogLink: String? = "", isWhatsApp: Boolean?, isFb: Boolean?
    ) {
      val customPagesTemplate = StringBuilder()
      if (pageName.isNullOrBlank().not() && link.isNullOrBlank().not()) {
        customPagesTemplate.append("🆕 Read about $pageName. $link").append("\n")
      }
      if (catalogLink.isNullOrBlank().not()) {
        customPagesTemplate.append("🏷️ Check our online catalogue, $catalogLink").append("\n")
      }
      if (catalogLink.isNullOrBlank().not()) {
        customPagesTemplate.append("📞 Feel free to call $vmn if you need any help.")
      }

      share(activity = activity, customPagesTemplate.toString(), isWhatsApp = isWhatsApp, isFb = isFb)
    }

    fun shareTestimonial(
      activity: Activity, testimonialContent: String, customerName: String, link: String?,
      catalogLink: String, vmn: String, isWhatsApp: Boolean?
    ) {
      val testimonialTemplate = StringBuilder()
      if (testimonialContent.isNullOrBlank().not()) {
        testimonialTemplate.append("${truncateString(testimonialContent, 100)}*").append("\n")
      }
      if (customerName.isNullOrBlank().not() && link.isNullOrBlank().not()) {
        testimonialTemplate.append("💁 See what *$customerName* has to say about us $link")
          .append("\n")
      }
      if (catalogLink.isNullOrBlank().not()) {
        testimonialTemplate.append("🏷️ Check our online catalogue, $catalogLink").append("\n")
      }
      if (vmn.isNullOrBlank().not()) {
        testimonialTemplate.append("📞 Feel free to call $vmn if you need any help.")
      }
      share(activity = activity, testimonialTemplate.toString(), isWhatsApp = isWhatsApp)
    }

    fun shareWebsiteTheme(
      activity: Activity, businessName: String, websiteLink: String, vmn: String, isWhatsApp: Boolean? = false,
      isFb: Boolean? = false, isTwitter: Boolean? = false, isLinkedin: Boolean? = false, isCopy: Boolean? = false
    ) {
      val webSiteThemeTemplate = StringBuilder()
      if (businessName.isNullOrBlank().not()) {
        webSiteThemeTemplate.append("Greetings from $businessName.").append("\n")
      }
      if (websiteLink.isNullOrBlank().not()) {
        webSiteThemeTemplate.append("\uD83C\uDF10 Check our website for the latest updates and offers $websiteLink.")
          .append("\n")
      }
      if (vmn.isNullOrBlank().not()) {
        webSiteThemeTemplate.append("\uD83D\uDCDE For any query, call: $vmn")
      }
      if (isCopy == true) {
        setClipboard(context = activity, webSiteThemeTemplate.toString())
        return
      }
      share(activity, webSiteThemeTemplate.toString(), isWhatsApp = isWhatsApp, isFb = isFb, isTwitter = isTwitter, isLinkedin = isLinkedin)
    }

    private fun setClipboard(context: Context, text: String) {
      Toast.makeText(context, "Copied!", Toast.LENGTH_LONG).show()
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("Copied Text", text)
      clipboard.setPrimaryClip(clip)
    }

    fun share(
      activity: Activity, shareText: String, imageUri: String? = null, isWhatsApp: Boolean? = false,
      isFb: Boolean? = false, isTwitter: Boolean? = false, isLinkedin: Boolean? = false
    ) {
      if (NetworkUtils.isNetworkConnected()) {
        val target: Target = object : Target {
          override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            targetMap = null
            val imageName = "image_${DateUtils.getCurrentDate().time}.png"
            val filesDir: File = activity.applicationContext.filesDir
            val imageFile = File(filesDir, imageName)
            try {
              val os = FileOutputStream(imageFile)
              bitmap.compress(Bitmap.CompressFormat.PNG, 70, os)
              os.flush()
              os.close()
            } catch (e: java.lang.Exception) {
              Log.e(javaClass.simpleName, "Error writing bitmap", e)
              SentryController.captureException(e)
            }
            val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.provider", imageFile)
            shareUriWithText(activity, uri, shareText, isWhatsApp, isFb, isLinkedin, isTwitter)
          }

          override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
            targetMap = null
          }

          override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
          }
        }
        if (imageUri.isNullOrEmpty().not()) {
          targetMap = target
          Picasso.get().load(imageUri ?: "").into(target)
        } else {
          shareUriWithText(activity, null, shareText, isWhatsApp, isFb, isLinkedin, isTwitter)
        }
      }
    }

    private fun shareUriWithText(
      activity: Activity, uri: Uri?, shareText: String, isWhatsApp: Boolean?, isFb: Boolean?,
      isLinkedin: Boolean?, isTwitter: Boolean?, intentChooserTitle: String? = BaseApplication.instance.getString(R.string.app_name)
    ) {
      val share = Intent(Intent.ACTION_SEND)
      share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      share.putExtra(Intent.EXTRA_TEXT, shareText)
      share.type = if (uri != null) "image/*" else "text/plain"
      uri?.let {
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      if (share.resolveActivity(activity.packageManager) != null) {
        try {
          if (isWhatsApp == true)
            share.setPackage(activity.getString(R.string.whatsapp_package))
          if (isFb == true)
            share.setPackage(activity.getString(R.string.facebook_package))
          if (isLinkedin == true)
            share.setPackage("com.linkedin.android")
          if (isTwitter == true)
            share.setPackage(activity.getString(R.string.twitter_package))
          activity.startActivity(Intent.createChooser(share, intentChooserTitle))
        } catch (e: ActivityNotFoundException) {
          e.printStackTrace()
          SentryController.captureException(e)
          activity.startActivity(Intent.createChooser(share, intentChooserTitle))
        }
      }
    }
  }
}
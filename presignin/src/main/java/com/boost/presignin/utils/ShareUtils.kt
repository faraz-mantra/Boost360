package com.boost.presignin.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignin.R
import com.framework.analytics.SentryController

enum class ShareType(var id: String, var message: String) {
  WHATS_APP("com.whatsapp", "Whatsapp have not been installed!"),
  MESSENGER("com.facebook.orca", "Messenger have not been installed!"),
  INSTAGRAM("com.instagram.android", "Instagram have not been installed!"),
  TELEGRAM("org.telegram.messenger", "Telegram have not been installed!"),
  G_MAIL("com.google.android.gm", "Gmail have not been installed!");
}

fun AppCompatActivity.shareViaAnyApp(shareType: ShareType?, message: String) {
  val intent = Intent(Intent.ACTION_SEND)
  intent.type = "text/plain"
  shareType?.id?.let { intent.setPackage(it) }
  intent.putExtra(Intent.EXTRA_TEXT, message)
  try {
    if (shareType != null) this.startActivity(intent)
    else this.startActivity(
      Intent.createChooser(
        intent,
        resources.getString(R.string.share_your_business)
      )
    )
  } catch (ex: ActivityNotFoundException) {
    SentryController.captureException(ex)

    Toast.makeText(
      this,
      shareType?.message ?: "Sharing error, please try again!",
      Toast.LENGTH_SHORT
    ).show()
    shareType?.id?.let {
      startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse("http://play.google.com/store/apps/details?id=$it")
        )
      )
    }
  }
}
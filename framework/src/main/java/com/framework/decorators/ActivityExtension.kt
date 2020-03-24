package com.framework.decorators

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.openUrl(url: String) {
  val i = Intent(Intent.ACTION_VIEW)
  i.data = Uri.parse(url)
  startActivity(i)
}
package com.boost.marketplace.utils

import android.content.Context
import com.framework.analytics.SentryController
import java.io.IOException

object Utils {

  fun getAssetJsonData(context: Context): String? {
    val json: String
    try {
      val inputStream = context.getAssets().open("category_model_v3.json")
      val size = inputStream.available()
      val buffer = ByteArray(size)
      inputStream.use { it.read(buffer) }
      json = String(buffer)
    } catch (ioException: IOException) {
      ioException.printStackTrace()
      SentryController.captureException(ioException)
      return null
    }
    return json
  }

}

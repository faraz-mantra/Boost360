package com.nowfloats.education.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import com.nowfloats.education.helper.Constants.DEFAULT_BITMAP_FORMAT
import com.nowfloats.education.helper.Constants.JPEG
import com.nowfloats.education.helper.Constants.PICTURE_QUALITY_40
import com.nowfloats.education.helper.Constants.PNG
import com.nowfloats.education.helper.Constants.WEBP
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SaveImageHelper(private val context: Context) {
  private val TAG = SaveImageHelper::class.java.simpleName

  fun writePhotoFile(
    bitmap: Bitmap?, photoName: String, directoryName: String = "",
    format: Bitmap.CompressFormat = DEFAULT_BITMAP_FORMAT, autoIncrementNameByDate: Boolean = false
  ): String? {
    var photoName = photoName

    if (bitmap == null) {
      return null
    } else {
      val bytes = ByteArrayOutputStream()
      bitmap.compress(format, PICTURE_QUALITY_40, bytes)
      val df = SimpleDateFormat("yyyyMMddHHmmss")
      val date = df.format(Calendar.getInstance().time)
      //Update the System
      when (format) {
        PNG -> {
          photoName =
            if (autoIncrementNameByDate) photoName + "_" + date + ".png" else "$photoName.png"
        }
        JPEG -> {
          photoName =
            if (autoIncrementNameByDate) photoName + "_" + date + ".jpeg" else "$photoName.jpeg"
        }
        WEBP -> {
          photoName =
            if (autoIncrementNameByDate) photoName + "_" + date + ".webp" else "$photoName.webp"
        }
      }

      var wallpaperDirectory: File? = null

      wallpaperDirectory = try {
        /*if (directoryName == "") {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/")
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + directoryName + "/")
        }*/

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
          if (directoryName == "") {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/")
          } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + directoryName + "/")
          }
        } else {
          if (directoryName == "") {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/")
          } else {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + directoryName + "/")
          }
        }

      } catch (ev: Exception) {
        try {
          Environment.getExternalStorageDirectory()
        } catch (ex: Exception) {
          try {
            Environment.getDataDirectory()
          } catch (e: Exception) {
            Environment.getRootDirectory()
          }
        }
      }

      if (wallpaperDirectory != null) {
        if (!wallpaperDirectory.exists()) {
          wallpaperDirectory.exists()
          wallpaperDirectory.mkdirs()
        }

        val f = File(wallpaperDirectory, photoName)
        try {
          f.createNewFile()
          val fo = FileOutputStream(f)
          fo.write(bytes.toByteArray())
          fo.close()
          context.sendBroadcast(
            Intent(
              Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
              Uri.parse("file://" + f.absolutePath)
            )
          )

          try {
            //Update the System
            val u = Uri.parse(f.absolutePath)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, u))

          } catch (ex: Exception) {
            Log.e(TAG, "Exception : $ex")
          }
          return f.absolutePath
        } catch (ex: Exception) {
          Log.e(TAG, "Exception : $ex")
          return null
        }
      } else {
        return null
      }
    }
  }
}
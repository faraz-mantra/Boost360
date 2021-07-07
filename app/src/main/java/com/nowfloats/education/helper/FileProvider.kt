package com.nowfloats.education.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.thinksity.BuildConfig
import java.io.File

class FileProvider(private val context: Context) {

  fun createImageUri(fileName: String): Uri? {
    return fileName.run {
      if (!isNullOrBlank())
        cameraImageUri(this.plus(Constants.JPEG_FORMAT))
      else Uri.EMPTY
    }
  }

  private fun cameraImageUri(fileName: String): Uri {
    val storageDir = context.getExternalFilesDir(DIRECTORY)
    val photoFile = File(storageDir, fileName)
    return FileProvider.getUriForFile(
      context,
      AUTHORITY_PROVIDER,
      photoFile
    )
  }

  companion object {
    private const val DIRECTORY = "Profile"
    private const val AUTHORITY_PROVIDER = "${BuildConfig.APPLICATION_ID}.provider"
  }
}

fun Activity.getBitmapFromUri(uri: Uri?): Bitmap? =
  MediaStore.Images.Media.getBitmap(contentResolver, uri)
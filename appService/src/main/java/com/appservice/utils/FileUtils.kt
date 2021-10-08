package com.appservice.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import com.framework.analytics.SentryController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt


class FileUtils(var context: Activity) {

  fun getPath(uri: Uri): String? {
    // check here to KITKAT or new version
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    // DocumentProvider
    if (isKitKat) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).toTypedArray()
        val type = split[0]
        val fullPath = getPathFromExtSD(split)
        return if (fullPath !== "") {
          fullPath
        } else {
          null
        }
      }
      // DownloadsProvider
      if (isDownloadsDocument(uri)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          var cursor: Cursor? = null
          try {
            cursor = context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
            if (!(cursor == null || !cursor.moveToFirst())) {
              val fileName: String = cursor.getString(0)
              val path: String = Environment.getExternalStorageDirectory().toString()
                .toString() + "/Download/" + fileName
              if (!TextUtils.isEmpty(path)) {
                return path
              }
            }
          } finally {
            cursor?.close()
          }
          val id: String = DocumentsContract.getDocumentId(uri)
          if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
              return id.replaceFirst("raw:".toRegex(), "")
            }
            val contentUriPrefixesToTry = arrayOf(
              "content://downloads/public_downloads",
              "content://downloads/my_downloads",
              "content://downloads/all_downloads"
            )
            for (contentUriPrefix in contentUriPrefixesToTry) {
              return try {
                val contentUri: Uri = ContentUris.withAppendedId(
                  Uri.parse(contentUriPrefix),
                  java.lang.Long.valueOf(id)
                )
                getDataColumn(context, contentUri, null, null)
              } catch (e: NumberFormatException) {
                SentryController.captureException(e)
                //In Android 8 and Android P the id is not a number
                uri.path?.replaceFirst("^/document/raw:", "")?.replaceFirst("^raw:", "")
              }
            }
          }
        } else {
          val id = DocumentsContract.getDocumentId(uri)
          if (id.startsWith("raw:")) {
            return id.replaceFirst("raw:".toRegex(), "")
          }
          try {
            contentUri = ContentUris.withAppendedId(
              Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
          } catch (e: NumberFormatException) {
            SentryController.captureException(e)
            e.printStackTrace()
          }
          if (contentUri != null) {
            return getDataColumn(context, contentUri, null, null)
          }
        }
      }
      // MediaProvider
      if (isMediaDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).toTypedArray()
        val type = split[0]
        val contentUri: Uri? = when (type) {
          "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
          "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
          "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
          else -> MediaStore.Files.getContentUri("external");
        }
        selection = "_id=?"
        selectionArgs = arrayOf(split[1])
        return getDataColumn(
          context, contentUri, selection,
          selectionArgs
        )
      }
      if (isGoogleDriveUri(uri)) {
        return getDriveFilePath(uri)
      }
      if (isWhatsAppFile(uri)) {
        return getFilePathForWhatsApp(uri)
      }
      if ("content".equals(uri.scheme, ignoreCase = true)) {
        if (isGooglePhotosUri(uri)) {
          return uri.lastPathSegment
        }
        if (isGoogleDriveUri(uri)) {
          return getDriveFilePath(uri)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) copyFileToInternalStorage(
          uri,
          "userfiles"
        )
        else getDataColumn(context, uri, null, null)

      }
      if ("file".equals(uri.scheme, ignoreCase = true)) return uri.path
    } else {
      if (isWhatsAppFile(uri)) return getFilePathForWhatsApp(uri)
      if ("content".equals(uri.scheme, ignoreCase = true)) {
        val projection = arrayOf(
          MediaStore.Images.Media.DATA
        )
        var cursor: Cursor? = null
        try {
          cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
          val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
          if (cursor?.moveToFirst()!!) {
            return cursor.getString(columnIndex)
          }
        } catch (e: Exception) {
          SentryController.captureException(e)
          e.printStackTrace()
        }
      }
    }
    return null
  }

  private fun fileExists(filePath: String): Boolean {
    val file = File(filePath)
    return file.exists()
  }

  private fun getPathFromExtSD(pathData: Array<String>): String {
    val type = pathData[0]
    val relativePath = "/" + pathData[1]
    var fullPath = ""

    // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
    // something like "71F8-2C0A", some kind of unique id per storage
    // don't know any API that can get the root path of that storage based on its id.
    //
    // so no "primary" type, but let the check here for other devices
    if ("primary".equals(type, ignoreCase = true)) {
      fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
      if (fileExists(fullPath)) {
        return fullPath
      }
    }

    // Environment.isExternalStorageRemovable() is `true` for external and internal storage
    // so we cannot relay on it.
    //
    // instead, for each possible path, check if file exists
    // we'll start with secondary storage as this could be our (physically) removable sd card
    fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
    if (fileExists(fullPath)) {
      return fullPath
    }
    fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
    return if (fileExists(fullPath)) {
      fullPath
    } else fullPath
  }

  private fun getDriveFilePath(uri: Uri): String {
    val returnUri: Uri = uri
    val returnCursor: Cursor? = context.contentResolver.query(returnUri, null, null, null, null)
    /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
    val nameIndex: Int = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: 0
    val sizeIndex: Int = returnCursor?.getColumnIndex(OpenableColumns.SIZE) ?: 0
    returnCursor?.moveToFirst()
    val name: String = returnCursor?.getString(nameIndex) ?: ""
    val size = returnCursor?.getLong(sizeIndex).toString()
    val file = File(context.cacheDir, name)
    try {
      val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(file)
      var read = 0
      val maxBufferSize = 1 * 1024 * 1024
      val bytesAvailable: Int? = inputStream?.available()

      //int bufferSize = 1024;
      val bufferSize = bytesAvailable?.coerceAtMost(maxBufferSize) ?: 0
      val buffers = ByteArray(bufferSize)
      while (inputStream?.read(buffers).also { read = it!! } != -1) {
        outputStream.write(buffers, 0, read)
      }
      Log.e("File Size", "Size " + file.length())
      inputStream?.close()
      outputStream.close()
      Log.e("File Path", "Path " + file.path)
      Log.e("File Size", "Size " + file.length())
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.e("Exception", e.message ?: "")
    }
    return file.path
  }

  /***
   * Used for Android Q+
   * @param uri
   * @param newDirName if you want to create a directory, you can set this variable
   * @return
   */
  private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String {
    val returnUri: Uri = uri
    val returnCursor: Cursor? = context.contentResolver.query(
      returnUri,
      arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
      null,
      null,
      null
    )

    /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
    val nameIndex: Int = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: 0
    val sizeIndex: Int = returnCursor?.getColumnIndex(OpenableColumns.SIZE) ?: 0
    returnCursor?.moveToFirst()
    val name: String = returnCursor?.getString(nameIndex) ?: ""
    val size = returnCursor?.getLong(sizeIndex).toString()
    val output: File
    if (newDirName != "") {
      val dir = File(context.filesDir.toString() + "/" + newDirName)
      if (!dir.exists()) {
        dir.mkdir()
      }
      output = File(context.filesDir.toString() + "/" + newDirName + "/" + name)
    } else {
      output = File(context.filesDir.toString() + "/" + name)
    }
    try {
      val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(output)
      var read = 0
      val bufferSize = 1024
      val buffers = ByteArray(bufferSize)
      while (inputStream?.read(buffers).also { read = it!! } != -1) {
        outputStream.write(buffers, 0, read)
      }
      inputStream?.close()
      outputStream.close()
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.e("Exception", e.message ?: "")
    }
    return output.path
  }

  private fun getFilePathForWhatsApp(uri: Uri): String {
    return copyFileToInternalStorage(uri, "whatsapp")
  }

  private fun getDataColumn(context: Activity, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    if (uri != null) {
      var cursor: Cursor? = null
      val column = "_data"
      val projection = arrayOf(column)
      try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
          val index: Int = cursor.getColumnIndexOrThrow(column)
          return cursor.getString(index)
        }
      } finally {
        cursor?.close()
      }
    }
    return null
  }

  private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
  }

  private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
  }

  private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
  }

  private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
  }

  fun isWhatsAppFile(uri: Uri): Boolean {
    return "com.whatsapp.provider.media" == uri.authority
  }

  private fun isGoogleDriveUri(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
  }

  companion object {
    private var contentUri: Uri? = null
  }

  fun getBitmap(path: String?, mWidth: Int): Bitmap? {
    if (path == null) return null
    val uri = Uri.fromFile(File(path))
    val rotation: Float = rotationForImage(context, uri)
    val matrix = Matrix()
    val TAG = "GetBitmap"
    var `in`: InputStream? = null
    return try {
      val IMAGE_MAX_WIDTH = mWidth
      val mContentResolver = context.contentResolver
      `in` = mContentResolver.openInputStream(uri)

      // Decode image size
      val o = BitmapFactory.Options()
      o.inJustDecodeBounds = true
      BitmapFactory.decodeStream(`in`, null, o)
      `in`!!.close()
      var scale = 1
      if (o.outWidth > mWidth) scale = (o.outWidth.toFloat() / mWidth.toFloat()).roundToInt()
      val optionsOut = BitmapFactory.Options()
      optionsOut.inSampleSize = scale
      optionsOut.inPurgeable = true
      var b: Bitmap? = null
      `in` = mContentResolver.openInputStream(uri)
      b = BitmapFactory.decodeStream(`in`, null, optionsOut)
      if (rotation != 0f) matrix.preRotate(rotation)
      if (b != null) b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)
      System.gc()
      `in`?.close()
      b
    } catch (e: IOException) {
      SentryController.captureException(e)
      null
    } catch (e: OutOfMemoryError) {
      e.printStackTrace()
      null
    }
  }

  fun rotationForImage(context: Context, uri: Uri): Float {
    if (uri.scheme == "content") {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
        val c = context.contentResolver.query(uri, projection, null, null, null)
        if (c?.moveToFirst() == true) return c.getInt(0).toFloat()
      } else return 0F
    } else if (uri.scheme == "file") {
      try {
        val exif = ExifInterface(uri.path!!)
        val rotation = exifOrientationToDegrees(
          exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
          )
        ) as? Int
        return rotation?.toFloat() ?: 0F
      } catch (e: IOException) {
        SentryController.captureException(e)
        e.printStackTrace()
      }
    }
    return 0f
  }

  private fun exifOrientationToDegrees(exifOrientation: Int): Float {
    return when (exifOrientation) {
      ExifInterface.ORIENTATION_ROTATE_90 -> 90F
      ExifInterface.ORIENTATION_ROTATE_180 -> 180F
      ExifInterface.ORIENTATION_ROTATE_270 -> 270F
      else -> 0F
    }
  }
}
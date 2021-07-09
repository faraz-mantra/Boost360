package com.framework.imagepicker

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.*
import java.util.*


/**
 * Created by Sarvare Alam on 5/21/2018.
 */
object Utility {
    private const val TAG = "Utility"

    @Throws(IOException::class)
    fun compressImage(path: String): String {
        val file = File(path)
        val bitmap = BitmapFactory.decodeFile(path)
        val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return path
    }

    // return SystemClock.currentThreadTimeMillis()+"";
    val randomString: String
        get() =// return SystemClock.currentThreadTimeMillis()+"";
            UUID.randomUUID().toString()

    fun createFolder(path: String) {
        try {
            val dir = File(path.substring(0, path.lastIndexOf("/")))
            Log.d(TAG, "createFolder: " + dir.exists())
            if (!dir.exists()) {
                dir.mkdirs()
            }
        } catch (ex: Exception) {
            Log.w(TAG, "creating file error: ", ex)
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            column_index?.let { cursor?.getString(it) } ?:""
        } finally {
            cursor?.close()
        }
    }

    @WorkerThread
    @Throws(IOException::class)
    fun compressAndRotateIfNeeded(sourceFile: File, destinationFile: File, value: Int, reqWidth: Int, reqHeight: Int) {
        val path = sourceFile.absolutePath
        val bounds = BitmapFactory.Options()
        var bm: Bitmap?
        if (reqHeight != 0 && reqWidth != 0) {
            bounds.inJustDecodeBounds = true
            bm = BitmapFactory.decodeFile(path, bounds)
            bounds.inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
            bounds.inJustDecodeBounds = false
        }
        bm = BitmapFactory.decodeFile(path, bounds)
        if (bm == null) {
            Log.d("compress", "bitmap is null")
            return
        }
        val rotationAngle = getCameraPhotoOrientation(sourceFile)
        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat(), bm.width.toFloat() / 2, bm.height.toFloat() / 2)
        val rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight,
                matrix, true)
        val fos = FileOutputStream(destinationFile.absoluteFile)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, value, fos)
        fos.flush()
        fos.close()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                    && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @WorkerThread
    @Throws(IOException::class)
    private fun getCameraPhotoOrientation(file: File): Int {
        val exif = ExifInterface(
                file.absolutePath)
        val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)
        var rotate = 0
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            else -> {
            }
        }
        return rotate
    }
}

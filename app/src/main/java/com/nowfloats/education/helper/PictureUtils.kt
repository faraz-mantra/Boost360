package com.nowfloats.education.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import kotlin.math.min
import kotlin.math.roundToInt

class PictureUtils {

  //Rotate the bitmap if the image is in landscape camera
  fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val retVal: Bitmap
    val matrix = Matrix()
    matrix.postRotate(angle)
    retVal = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    return retVal
  }

  // This method is used to create scaled bitmap
  fun createScaledBitmap(bitmap: Bitmap, width: Int, height: Int, filter: Boolean): Bitmap? {
    return Bitmap.createScaledBitmap(bitmap, width, height, filter)
  }

  // This method is used to resize bitmap
  fun resizePhoto(
    realImage: Bitmap, maxImageSize: Float,
    filter: Boolean
  ): Bitmap {
    val ratio = min(
      maxImageSize / realImage.width,
      maxImageSize / realImage.height
    )
    val width = (ratio * realImage.width).roundToInt()
    val height = (ratio * realImage.height).roundToInt()

    return Bitmap.createScaledBitmap(
      realImage, width,
      height, filter
    )
  }
}
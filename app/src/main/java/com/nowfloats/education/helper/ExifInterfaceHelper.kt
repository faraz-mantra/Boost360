package com.nowfloats.education.helper

import android.graphics.Bitmap
import android.media.ExifInterface

class ExifInterfaceHelper(private val pictureUtils: PictureUtils) {

    fun rotateImageIfRequired(path: String?, bitmap: Bitmap): Bitmap {
        val exifInterface = ExifInterface(path?:"")

        return when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                pictureUtils.rotateImage(bitmap, 90F)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                pictureUtils.rotateImage(bitmap, 180F)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                pictureUtils.rotateImage(bitmap, 270F)
            }
            else -> bitmap
        }
    }
}
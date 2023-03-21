package com.framework.imagepicker

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import com.framework.R
import com.framework.utils.sizeInKb
import com.framework.utils.sizeInMb
import java.io.File

object BoostImageUtils {

    fun isBusinessLogoValid(activity: Activity, imgFile: File): String {
        if ((imgFile.extension.equals("JPEG", ignoreCase = true) ||
                    imgFile.extension.equals("JPG", ignoreCase = true) ||
                    imgFile.extension.equals("PNG", ignoreCase = true) ||
                    imgFile.extension.equals("GIF", ignoreCase = true)
                    ).not()) {
            return activity.getString(R.string.please_select_correct_image_file_format_for_business_logo)
        }

        val bitMapOption: BitmapFactory.Options = BitmapFactory.Options()
        bitMapOption.inJustDecodeBounds = true
        bitMapOption.inScaled = false
        BitmapFactory.decodeFile(imgFile.path, bitMapOption)
        val imageWidth: Int = bitMapOption.outWidth
        val imageHeight: Int = bitMapOption.outHeight

        if (imageWidth < 200 || imageHeight < 200) {
            return activity.getString(R.string.image_file_resolution_cannot_be_lesser_than_200)
        }

        val file_size: Int = (imgFile.length() / 1024).toString().toInt()
        Log.i("savedImageSize->", file_size.toString())

        if ((imgFile.sizeInMb <= 5).not()) {
            return activity.getString(R.string.image_cannot_be_bigger_that_5_mb)
        }

        return ""
    }

    fun isImageGalleryValid(activity: Activity, imgFile: File): String {
        if ((imgFile.extension.equals("JPEG", ignoreCase = true) ||
                    imgFile.extension.equals("JPG", ignoreCase = true) ||
                    imgFile.extension.equals("PNG", ignoreCase = true)
                    ).not()) {
            return activity.getString(R.string.please_select_correct_image_file_format_for_image_gallery)
        }

        val bitMapOption: BitmapFactory.Options = BitmapFactory.Options()
        bitMapOption.inJustDecodeBounds = true
        bitMapOption.inScaled = false
        BitmapFactory.decodeFile(imgFile.path, bitMapOption)
        val imageWidth: Int = bitMapOption.outWidth
        val imageHeight: Int = bitMapOption.outHeight

        if (imageWidth < 200 || imageHeight < 200) {
            return activity.getString(R.string.image_file_resolution_cannot_be_lesser_than_200)
        }

        val file_size: Int = (imgFile.length() / 1024).toString().toInt()
        Log.i("savedImageSize->", file_size.toString())

        if ((imgFile.sizeInMb <= 5).not()) {
            return activity.getString(R.string.image_cannot_be_bigger_that_5_mb)
        }

        return ""
    }
}
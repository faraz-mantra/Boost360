package com.framework.imagepicker

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

/**
 * Created by Sarvare Alam on 5/21/2018.
 */
internal object ImageProcessing {
    private const val TAG = "ImageProcessing"

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun processMultiImage(context: Context?, data: Intent): List<String?> {
        val listOfImgs: MutableList<String?> = ArrayList()
        val clipdata = data.clipData
        for (i in 0 until clipdata.itemCount) {
            val selectedImage = clipdata.getItemAt(i).uri
            val selectedImagePath = FileProcessing.getPath(context!!, selectedImage)
            listOfImgs.add(selectedImagePath)
        }
        return listOfImgs
    }
}

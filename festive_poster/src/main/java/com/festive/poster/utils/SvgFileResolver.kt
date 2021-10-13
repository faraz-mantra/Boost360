package com.festive.poster.utils

import android.graphics.Bitmap
import com.caverock.androidsvg.SVGExternalFileResolver
import com.festive.poster.FestivePosterApplication
import java.io.File
import android.graphics.BitmapFactory
import android.util.Log
import com.festive.poster.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class SvgFileResolver: SVGExternalFileResolver() {

    private val TAG = "SvgFileResolver"
    override fun resolveImage(filename: String?): Bitmap {
        val file = File(filename)
        Log.i(TAG, "resolveImage: ${file.absolutePath}")
        if (file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            return myBitmap
        }else{
          return  BitmapFactory.decodeResource(FestivePosterApplication.instance.resources, R.drawable.placeholder_image)
        }

    }
}
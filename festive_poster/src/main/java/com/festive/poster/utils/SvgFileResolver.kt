package com.festive.poster.utils

import android.graphics.Bitmap
import com.caverock.androidsvg.SVGExternalFileResolver
import com.festive.poster.FestivePosterApplication
import java.io.File
import android.graphics.BitmapFactory
import android.util.Log
import com.festive.poster.R


class SvgFileResolver: SVGExternalFileResolver() {

    private val TAG = "SvgFileResolver"
    override fun resolveImage(filename: String?): Bitmap {
        Log.i(TAG, "resolveImage: $filename")
        val file = File(filename)
        if (file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            return myBitmap
        }else{
          return  BitmapFactory.decodeResource(FestivePosterApplication.instance.resources, R.drawable.placeholder_image)
        }

    }
}
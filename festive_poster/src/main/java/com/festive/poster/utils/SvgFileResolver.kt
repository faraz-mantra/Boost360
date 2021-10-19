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
import java.io.IOException


class SvgFileResolver: SVGExternalFileResolver() {

    private val TAG = "SvgFileResolver"

    private val cache: HashMap<String, Bitmap> = HashMap()
    override fun resolveImage(filename: String?): Bitmap? {

        Log.i(TAG, "resolveImage: file name $filename")
        if(filename?.isEmpty() == true)
            return null

        if(cache.containsKey(filename))
            return cache.get(filename)

        try {
            val file = File(filename)
            if (file.exists()) {
                val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                filename?.let { cache.put(it, myBitmap) };
                return myBitmap
            } else {
                return BitmapFactory.decodeResource(
                    FestivePosterApplication.instance.resources,
                    R.drawable.placeholder_image
                )
            }
        } catch (e: IOException){
            return null
        }
    }
}
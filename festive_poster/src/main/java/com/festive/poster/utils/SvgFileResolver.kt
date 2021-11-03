package com.festive.poster.utils

import android.graphics.Bitmap
import com.caverock.androidsvg.SVGExternalFileResolver
import com.festive.poster.FestivePosterApplication
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.caverock.androidsvg.SVG
import com.festive.poster.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


class SvgFileResolver: SVGExternalFileResolver() {

    private val TAG = "SvgFileResolver"
    val FONT_WEIGHT_NORMAL = 400
    val FONT_WEIGHT_BOLD = 700
    val FONT_WEIGHT_LIGHTER = -1
    val FONT_WEIGHT_BOLDER = +1
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

    override fun resolveFont(fontFamily: String?, fontWeight: Int, fontStyle: String?): Typeface {
        var fontId = R.font.inter_regular
        when(fontWeight){
            FONT_WEIGHT_BOLD->{
                fontId = R.font.inter_medium
            }
            FONT_WEIGHT_BOLDER->{
                fontId = R.font.inter_bold
            }
            FONT_WEIGHT_LIGHTER->{
                fontId = R.font.inter_light
            }
        }

       return ResourcesCompat.getFont(FestivePosterApplication.instance,fontId)!!
    }
}
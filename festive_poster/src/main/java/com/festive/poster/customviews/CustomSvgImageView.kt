package com.festive.poster.customviews

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.festive.poster.models.KeyModel
import com.festive.poster.utils.FileUtils
import com.festive.poster.utils.SvgUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CustomSvgImageView: SVGImageView {

    private var loadAndReplaceJob: Deferred<Any?>?=null
    private var keys:List<KeyModel>?=null
    private var url: String?=null
    private val TAG = "CustomSvgImageView"
    private var svgString:String?=null

    constructor(context: Context) : super(context) {
//    setCustomAttrs(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//    setCustomAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
//    setCustomAttrs(context, attrs)

    }





    fun loadAndReplace(url:String,keys:List<KeyModel>?=null){
        this.url = url
        this.keys = keys
        loadAndReplaceJob = CoroutineScope(Dispatchers.IO).async {
            svgString = SvgUtils.getSvgAsAString(url)
            if (keys!=null){
                svgString =  SvgUtils.replace(svgString,keys)
            }
            withContext(Dispatchers.Main){
                setSVG(SVG.getFromString(svgString))
            }

        }

    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadAndReplaceJob?.cancel()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (url!=null){
            loadAndReplace(url!!,keys)
        }
    }


}
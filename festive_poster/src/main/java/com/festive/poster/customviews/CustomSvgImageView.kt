package com.festive.poster.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.festive.poster.models.PosterKeyModel
import com.festive.poster.utils.SvgUtils
import kotlinx.coroutines.*

class CustomSvgImageView: SVGImageView {

    private var loadAndReplaceJob: Deferred<Any?>?=null
    private var posterKeys:List<PosterKeyModel>?=null
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





    fun loadAndReplace(url:String?, posterKeys:List<PosterKeyModel>?=null){
        this.url = url
        this.posterKeys = posterKeys
        url?.let {
            Log.i(TAG, "loadAndReplace: url $url")
            loadAndReplaceJob = CoroutineScope(Dispatchers.IO).async {
                svgString = SvgUtils.getSvgAsAString(url)
                if (posterKeys!=null){
                    svgString =  SvgUtils.replace(svgString,posterKeys)
                }
                withContext(Dispatchers.Main){
                    setSVG(SVG.getFromString(svgString))
                }

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
            loadAndReplace(url!!,posterKeys)
        }
    }


}
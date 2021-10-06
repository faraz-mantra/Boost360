package com.festive.poster.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.festive.poster.utils.SvgUtils
import kotlinx.coroutines.*

class CustomSvgImageView: SVGImageView {

    private var map: Map<String, String>?=null
    private var url: String?=null
    private var downloadJob: Deferred<Any>?=null
    private var replaceJob: Deferred<Any>?=null
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



    fun loadFromUrl(url:String){
        this.url = url
        downloadJob = CoroutineScope(Dispatchers.IO).async{
            try {
                svgString = SvgUtils.getSvgAsAString(url)
                val svg = SVG.getFromString(svgString)
                withContext(Dispatchers.Main){
                    setSVG(svg)
                }
            }catch (e:Exception){
                if(e !is CancellationException)
                Log.e(TAG, "loadFromUrl: $e", )
            }


        }
    }

    fun replace(map: Map<String,String>){
        this.map=map
        replaceJob = CoroutineScope(Dispatchers.Default).async {

            if (downloadJob==null){
                return@async
            }

            downloadJob?.await()
            svgString=SvgUtils.replace(svgString,map)
            withContext(Dispatchers.Main){
                setSVG(SVG.getFromString(svgString))
            }
        }

    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        downloadJob?.cancel()
        replaceJob?.cancel()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (url!=null){
            loadFromUrl(url!!)

            if (map!=null){
                replace(map!!)
            }

        }
    }
}
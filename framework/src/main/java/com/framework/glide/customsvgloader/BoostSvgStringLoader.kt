package com.framework.glide.customsvgloader

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.caverock.androidsvg.PreserveAspectRatio
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.framework.glide.customsvgloader.SvgRenderCacheUtil.Companion.instance
import java.lang.ref.WeakReference

class BoostSvgStringLoader(
    var url: String,
    var model: List<PosterKeyModel>,
    var resource: CustomPictureDrawable,
    var context: Context,
    view: ImageView?
) : Runnable {
    var view: WeakReference<ImageView?>
    override fun run() {
        var svgString = instance.retrieveFromCache(url)
        if (svgString == null || svgString.isEmpty()) {
            svgString = resource.svgModel.convertedString
            svgString?.let { instance.saveToCache(url, it) }
        }
        if (svgString != null && !svgString.isEmpty()) {
            svgString = instance.replace(svgString, model, context)
            setSvg(svgString)
        }
    }

    fun setSvg(s: String?) {
        view.get()?.let {
            var svg: SVG? = null
            try {
                svg = SVG.getFromString(s)
                val op = RenderOptions().preserveAspectRatio(PreserveAspectRatio.FULLSCREEN)
                val picture = svg.renderToPicture(op)
                val drawable = PictureDrawable(picture)

            Looper.getMainLooper().run {

                Log.d("SvgLoader", "setSvg() called ${Thread.currentThread()}")
                Handler(Looper.getMainLooper()).post {
                    view.get()?.let {
                        Log.d("SvgLoader", "setSvg() called ${Thread.currentThread()}")
                        Glide.with(it.context.applicationContext).load(drawable).into(it)
//                        it.setImageDrawable(drawable);
                    }
                }
            }

            } catch (e: SVGParseException) {
                e.printStackTrace()
            }
        }
    }

    fun setSvgFromString(s: String?, view: ImageView) {
        var svg: SVG? = null
        try {
            svg = SVG.getFromString(s)
            val op = RenderOptions().preserveAspectRatio(PreserveAspectRatio.FULLSCREEN)
            val picture = svg.renderToPicture(op)
            val drawable = PictureDrawable(picture)
            view.setImageDrawable(drawable)
        } catch (e: SVGParseException) {
            e.printStackTrace()
        }
    }

    init {
        this.view = WeakReference(view)
    }
}
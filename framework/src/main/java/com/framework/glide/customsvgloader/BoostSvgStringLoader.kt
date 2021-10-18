package com.framework.glide.customsvgloader

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
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
            svgString = instance.replace(svgString, model, context)
        }
        if (svgString != null && !svgString.isEmpty()) {
            instance.saveToCache(url, svgString)

//            setSvgFromString(svgString, view);
        }
    }

    fun setSvg(s: String?) {
        if (view.get() != null) {
        }
    }

    fun setSvgFromString(s: String?, view: ImageView) {
        var svg: SVG? = null
        try {
            svg = SVG.getFromString(s)
            val picture = svg.renderToPicture()
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
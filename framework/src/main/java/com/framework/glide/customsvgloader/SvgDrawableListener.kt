package com.framework.glide.customsvgloader

import com.framework.glide.customsvgloader.SingletonExecutor.submit
import com.framework.glide.customsvgloader.CustomPictureDrawable
import com.framework.glide.customsvgloader.PosterKeyModel
import com.framework.glide.customsvgloader.BoostSvgStringLoader
import com.framework.glide.customsvgloader.SingletonExecutor
import com.caverock.androidsvg.SVG
import android.graphics.drawable.PictureDrawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVGParseException

class SvgDrawableListener : RequestListener<CustomPictureDrawable?> {
    var model: List<PosterKeyModel>? = null
    var url: String? = null
    var type:SvgRenderCacheUtil.SVG_TYPE?=null

    constructor() {}

    constructor(model: List<PosterKeyModel>?, url: String?,
                type: SvgRenderCacheUtil.SVG_TYPE) {
        this.model = model
        this.url = url
        this.type=type
    }

    constructor(type: SvgRenderCacheUtil.SVG_TYPE, url: String?,) {
        this.type=type
        this.url = url
    }
    override fun onLoadFailed(
        e: GlideException?,
        model: Any,
        target: Target<CustomPictureDrawable?>,
        isFirstResource: Boolean
    ): Boolean {
        val view = (target as ImageViewTarget<*>).view
        view.setLayerType(ImageView.LAYER_TYPE_NONE, null)
        return false
    }

    override fun onResourceReady(
        resource: CustomPictureDrawable?,
        model: Any,
        target: Target<CustomPictureDrawable?>,
        dataSource: DataSource,
        isFirstResource: Boolean
    ): Boolean {
        Log.d(
            "SvgDrawableListener",
            "onResourceReady() called with: resource " + Thread.currentThread()
        )
        val view = (target as ImageViewTarget<*>).view
        val v = BoostSvgStringLoader(url!!, this.model, resource!!,
            view.context, view,type!!)
        submit(v)
        //
//
//        String svgString = SvgRenderCacheUtil.Companion.getInstance().retrieveFromCache(url);
//        if (svgString == null || svgString.isEmpty()) {
//            svgString = resource.svgModel.getConvertedString();
//            svgString = SvgRenderCacheUtil.Companion.getInstance().replace(svgString, this.model, view.getContext());
//        }
//        if (svgString != null && !svgString.isEmpty()) {
//            SvgRenderCacheUtil.Companion.getInstance().saveToCache(url, svgString);
//            setSvgFromString(svgString, view);
//        }


//    view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        return true
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
}
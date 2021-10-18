package com.framework.glide.customsvgloader

import android.util.Log
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.caverock.androidsvg.PreserveAspectRatio
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.framework.utils.ConversionUtils
import java.io.IOException
import java.io.InputStream

class CustomSvgDecoder : ResourceDecoder<InputStream, SvgCustomDataModel> {

    override fun handles(source: InputStream, options: Options): Boolean {
        // TODO: Can we tell?
        Log.d("CustomSvgDecoder", "handles() called with: source = $source, options = $options")
        return true
    }

    override fun decode(
        source: InputStream, width: Int, height: Int, options: Options
    ): Resource<SvgCustomDataModel>? {
        Log.d("CustomSvgDecoder", "decode() called with: source = $options")
        val model = SvgCustomDataModel();
        model.inputStream = source
        model.convertedString = ConversionUtils.convertInputStreamToString(source)
        return SimpleResource(model)
    }

}
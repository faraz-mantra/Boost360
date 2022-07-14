package com.framework.glide.lottie_decoder

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import java.io.IOException
import java.io.InputStream

class LottieDecoder : ResourceDecoder<InputStream, LottieComposition> {

        override fun handles(source: InputStream, options: Options): Boolean = true

        @Throws(IOException::class)
        override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<LottieComposition> {
            return try {
                val lottieResult = LottieCompositionFactory.fromJsonInputStreamSync(source, null)
                SimpleResource(lottieResult.value!!)
            } catch (ex: Exception) {
                throw IOException("Cannot load lottie from stream", ex)
            }
        }
    }
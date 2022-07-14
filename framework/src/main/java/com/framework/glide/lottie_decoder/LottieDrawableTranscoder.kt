package com.framework.glide.lottie_decoder

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder

class LottieDrawableTranscoder : ResourceTranscoder<LottieComposition, LottieDrawable> {
        override fun transcode(toTranscode: Resource<LottieComposition>, options: Options): Resource<LottieDrawable> {
            val composition = toTranscode.get()
            val lottieDrawable = LottieDrawable()
            lottieDrawable.composition = composition
            lottieDrawable.repeatCount = LottieDrawable.INFINITE
            return SimpleResource(lottieDrawable)
        }
    }
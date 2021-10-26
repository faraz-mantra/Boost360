package com.framework.glide.customsvgloader

import java.util.concurrent.Executors

object SingletonExecutor {
    val executor = Executors.newFixedThreadPool(5)

    fun submit(runnable: BoostSvgStringLoader) {
        executor.submit(runnable)
    }
}
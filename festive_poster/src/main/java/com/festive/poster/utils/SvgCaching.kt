package com.festive.poster.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.collection.LruCache
import com.framework.analytics.SentryController

class SvgCaching private constructor() {

    private  val TAG = "SvgCaching"
    private object HOLDER {
        val INSTANCE = SvgCaching()
    }

    companion object {
        val instance: SvgCaching by lazy { HOLDER.INSTANCE }
    }
    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // Use 1/8th of the available memory for this memory cache.
    val cacheSize = maxMemory / 8
    val lru: LruCache<String, String> = LruCache(cacheSize)

    fun saveToCache(key: String, svgString: String) {

        Log.i(TAG, "saveToCache: ")
        try {
            SvgCaching.instance.lru.put(key, svgString)
        } catch (e: Exception) {
            SentryController.captureException(e)

            Log.e(TAG, "saveToCache: $e", )
        }

    }

    fun retrieveFromCache(key: String): String? {

        try {
            return SvgCaching.instance.lru.get(key) as String?
        } catch (e: Exception) {
            SentryController.captureException(e)

        }

        return null
    }

}
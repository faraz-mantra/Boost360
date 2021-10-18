package com.framework.glide.customsvgloader

import android.util.Log
import androidx.collection.LruCache

class SvgRenderCacheUtil private constructor() {
    private  val TAG = "SvgCaching"
    private object HOLDER {
        val INSTANCE = SvgRenderCacheUtil()
    }

    companion object {
        val instance: SvgRenderCacheUtil by lazy { HOLDER.INSTANCE }
    }
    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // Use 1/8th of the available memory for this memory cache.
    val cacheSize = maxMemory / 8
    val lru: LruCache<String, String> = LruCache(cacheSize)

    fun saveToCache(key: String, svgString: String) {

        Log.i(TAG, "saveToCache: ")
        try {
            SvgRenderCacheUtil.instance.lru.put(key, svgString)
        } catch (e: Exception) {
            Log.e(TAG, "saveToCache: $e", )
        }

    }

    fun retrieveFromCache(key: String): String? {

        try {
            return SvgRenderCacheUtil.instance.lru.get(key) as String?
        } catch (e: Exception) {
        }

        return null
    }
}
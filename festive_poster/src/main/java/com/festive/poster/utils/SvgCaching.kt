package com.festive.poster.utils

import android.graphics.Bitmap
import androidx.collection.LruCache

class SvgCaching private constructor() {

    private object HOLDER {
        val INSTANCE = SvgCaching()
    }

    companion object {
        val instance: SvgCaching by lazy { HOLDER.INSTANCE }
    }
    val lru: LruCache<Any, Any> = LruCache(1024)

    fun saveToCache(key: String, svgString: String) {

        try {
            SvgCaching.instance.lru.put(key, svgString)
        } catch (e: Exception) {
        }

    }

    fun retrieveFromCache(key: String): String? {

        try {
            return SvgCaching.instance.lru.get(key) as String?
        } catch (e: Exception) {
        }

        return null
    }

}
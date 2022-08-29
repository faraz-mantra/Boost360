package com.framework.glide.customsvgloader

import android.content.Context
import android.util.Log
import androidx.collection.LruCache
import com.framework.R
import com.framework.constants.PosterKeys
import com.framework.utils.fetchString
import java.io.File

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

    fun replace(
        svgString: String?,
        posterKeys: List<PosterKeyModel>?,
        context: Context,
    ): String? {
        var result =svgString

        posterKeys?.forEach {
            var replaceVal = if (it.custom!=null){
                it.custom
            } else it.default
//            Log.i(TAG, "replace: $replaceVal")
//            result = result?.replace("{{"+it.name+"}}",replaceVal.toString())
            if (it.type=="IMAGE"){
                Log.i(TAG, "replace: $replaceVal")

                if (replaceVal?.startsWith("https:") == true){
                    val fileName = replaceVal.substring(replaceVal.lastIndexOf("/")+1)
                    val file = File(FileUtils.getPathOfImages(context)+fileName)
                    if (!file.exists()){
                        FileUtils.saveImage(replaceVal,file, context)
                        Log.i(TAG, "image saved: ${file.path}")
                    }
                    if (file.exists())
                        result = result?.replace("{{"+it.name+"}}",file.path)
                    Log.i(TAG, "replace image path: ${it.name} ${file.path}")

                }else{

                    Log.i(TAG, "replace image path: ${replaceVal.toString()}")
                    result = result?.replace("{{"+it.name+"}}",replaceVal.toString())

                }
                result = result?.replace("{{"+it.name+"}}",replaceVal.toString())
            }else{
                if (replaceVal==null){
                   replaceVal =  getDefaultReplaceVal(it.name)
                }
                result = result?.replace("{{"+it.name+"}}",replaceVal.toString())
            }
        }

        return result
    }

    private fun getDefaultReplaceVal(name: String?): String? {
        return when(name){
            PosterKeys.business_email->{
                fetchString(R.string.your_email_address)
            }
            PosterKeys.business_name->{
                fetchString(R.string.your_business_name)
            }
            PosterKeys.user_name->{
                fetchString(R.string.your_name)
            }
            PosterKeys.user_contact->{
                fetchString(R.string.your_mobile_number)
            }
            else ->""

        }
    }


}
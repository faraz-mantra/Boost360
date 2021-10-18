package com.festive.poster.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.framework.glide.customsvgloader.PosterKeyModel
import com.framework.glide.GlideApp
import com.framework.glide.GlideRequest
import com.framework.glide.customsvgloader.CustomPictureDrawable
import com.framework.glide.customsvgloader.SvgDrawableListener
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.framework.glide.customsvgloader.FileUtils


object SvgUtils {

    var requestBuilder: GlideRequest<CustomPictureDrawable>? = null
    private val TAG = "SvgUtils"
    private var map = HashMap<String, SVG>()

    fun initReqBuilder(context: Context): GlideRequest<CustomPictureDrawable>? {
        if(requestBuilder == null){
            requestBuilder = GlideApp.with(context).`as`(CustomPictureDrawable::class.java)
        }
        return requestBuilder
    }

    fun loadImage(url: String, view: ImageView, model: List<PosterKeyModel>){
        val uri = Uri.parse(url)
        val listener = SvgDrawableListener(model, url)
        Log.d(TAG, "loadImage() called with: url = $url, model = $model $view")

        initReqBuilder(view.context)?.let{
            it.listener(listener)?.load(uri)?.into(view)
            return
        }
        Log.d(TAG, "loadImage() called with: url = $url, model = $model request builder is null")

    }


    suspend fun getSvgAsAString(url:String): String? {

        try {
//            val cacheString = SvgCaching.instance.retrieveFromCache(url)
//
//            if (cacheString!=null){
//                return cacheString
//            }
            getInputStream(url)?.let {
                val  reader = BufferedReader(
                    InputStreamReader(it)
                )

                // do reading, usually loop until end of file reading
                var mLine:StringBuilder = StringBuilder()
                var line:String?=""
                while (line!= null) {
                    line = reader.readLine()
                    //process line
                    if (line!=null)
                        mLine.append(line)
                }

                var result =mLine.toString()
                SvgCaching.instance.saveToCache(url,result)
                Log.i(TAG, "downloadSvg: $result")
                return result
            }
        }catch (e:Exception){
            Log.e(TAG, "getSvgAsAString: $e")
        }




        return null
    }

    suspend fun getInputStream(url: String): InputStream? {
        try {
            val okHttp = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).build()
            val response = okHttp.newCall(request).execute()
            val body = response.body
            val responseCode = response.code

            if (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
                body != null
            ) {
                // Read the file
                return body.byteStream()
            }

        }catch (e:Exception){
            Log.e(TAG, "getInputStream: $e")
        }
        return null
    }



    suspend fun replace(svgString: String?, posterKeys: List<PosterKeyModel>): String? {
        var result =svgString

        posterKeys.forEach {
            val replaceVal = if (it.custom==null) it.default else it.custom

            if (it.type=="IMAGE"){
                Log.i(TAG, "replace: $replaceVal")

                if (replaceVal?.startsWith("https:") == true){
//                    val fileName = replaceVal.substring(replaceVal.lastIndexOf("/")+1)
//                    val file = File(FileUtils.getPathOfImages()+fileName)
//                    if (!file.exists()){
////                        FileUtils.saveImage(replaceVal,file)
//                        Log.i(TAG, "image saved: ${file.path}")
//                    }
//                    if (file.exists())
//                        result = result?.replace("{{"+it.name+"}}",file.path)
//                    Log.i(TAG, "replace image path: ${file.path}")

                }else{

                    Log.i(TAG, "replace image path: ${replaceVal.toString()}")
                    result = result?.replace("{{"+it.name+"}}",replaceVal.toString())

                }

            }else{
                result = result?.replace("{{"+it.name+"}}",replaceVal.toString())
            }
        }

        return result
    }

    fun saveSvgCache(url: String, svg: SVG){
        map.put(url, svg)
    }

    fun getSvg(url: String):SVG?{
        return map.get(url)
    }





}
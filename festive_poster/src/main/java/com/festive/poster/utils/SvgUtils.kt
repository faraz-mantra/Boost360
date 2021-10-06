package com.festive.poster.utils

import android.util.Log
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.festive.poster.models.KeyModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit


object SvgUtils {

    private val TAG = "SvgUtils"


    suspend fun getSvgAsAString(url:String): String? {

        try {
            val cacheString = SvgCaching.instance.retrieveFromCache(url)

            if (cacheString!=null){
                return cacheString
            }
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
            Log.e(TAG, "getSvgAsAString: $e", )
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
            Log.e(TAG, "getInputStream: $e", )
        }
        return null
    }



    suspend fun replace(svgString: String?,keys: List<KeyModel>): String? {
        var result =svgString

        keys.forEach {
            if (it.Type=="Image"){
                Log.i(TAG, "replace: ${it.CustomValue}")

                val fileName = it.CustomValue.substring(it.CustomValue.lastIndexOf("/")+1)
                val file = File(FileUtils.getPathOfImages()+fileName)
                if (!file.exists()){
                    FileUtils.saveImage(it.CustomValue,file)
                    Log.i(TAG, "image saved: ${file.path}")

                }
               result = result?.replace(it.Name,fileName)
            }else{
                result = result?.replace(it.Name,it.CustomValue)
            }
        }
        return result
    }
}
package com.festive.poster.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.festive.poster.FestivePosterApplication
import com.festive.poster.models.PosterKeyModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection


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



    suspend fun replace(svgString: String?, posterKeys: List<PosterKeyModel>): String? {
        var result =svgString

        posterKeys.forEach {
            val replaceVal = if (it.Custom==null) it.Default else it.Custom

            if (it.Type=="Image"){
                Log.i(TAG, "replace: $replaceVal")

                val fileName = replaceVal?.substring(replaceVal.lastIndexOf("/")+1)
                val file = File(FileUtils.getPathOfImages()+fileName)
                if (!file.exists()){
                    FileUtils.saveImage(replaceVal,file)
                    Log.i(TAG, "image saved: ${file.path}")
                }
                if (file.exists())
                    result = result?.replace(it.Name,fileName.toString())
            }else{
                result = result?.replace(it.Name,replaceVal.toString())
            }
        }
        return result
    }



}
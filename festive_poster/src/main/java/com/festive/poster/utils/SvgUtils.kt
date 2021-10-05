package com.festive.poster.utils

import android.util.Log
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit


object SvgUtils {

    private val TAG = "SvgUtils"


    suspend fun getSvgAsAString(url:String): String? {

        try {
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

    fun replace(svgString:String?,map: Map<String,String>): String? {
        var result =svgString
        map.keys.forEach {key->
            Log.i(TAG, "replace: key $key value ${map[key]}")

            map[key]?.let {
                    value ->result=  result?.replace(key, value)
            }
        }

        return result
    }
}
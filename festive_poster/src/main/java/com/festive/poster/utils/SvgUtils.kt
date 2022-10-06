package com.festive.poster.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.framework.glide.GlideApp
import com.framework.glide.GlideRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.festive.poster.models.PosterModel
import com.festive.poster.utils.SvgUtils.getSvgAsAString
import com.framework.BaseApplication
import com.framework.analytics.SentryController
import com.framework.constants.PackageNames
import com.framework.glide.customsvgloader.*
import com.framework.utils.RegexUtils
import com.framework.utils.application
import com.framework.utils.saveImageToStorage
import com.framework.utils.shareAsImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object SvgUtils {

    var requestBuilder: GlideRequest<CustomPictureDrawable>? = null
    private val TAG = "SvgUtils"
    private var map = HashMap<String, SVG>()

    fun initReqBuilder(context: Context): GlideRequest<CustomPictureDrawable>? {
        if(requestBuilder == null){
            requestBuilder = GlideApp.with(context.applicationContext).`as`(CustomPictureDrawable::class.java).diskCacheStrategy(
                DiskCacheStrategy.DATA)
        }
        return requestBuilder
    }

    fun loadImage(url: String?, view: ImageView,
                  model: List<PosterKeyModel>?=null,
                  svgType: SvgRenderCacheUtil.SVG_TYPE=SvgRenderCacheUtil.SVG_TYPE.FESTIVE_POSTER,
                  ){
        if (url==null){
            return
        }
        val uri = Uri.parse(url)
        val listener = SvgDrawableListener(model, url,svgType)
        Log.d(TAG, "loadImage() called with: url = $url, model = $model $view")

        initReqBuilder(view.context)?.let{
            it.listener(listener)?.load(uri)?.into(view)
            return
        }
        Log.d(TAG, "loadImage() called with: url = $url, model = $model request builder is null")

    }

    fun loadImage(url: String?, view: ImageView,
                  ){
        loadImage(url, view,null, SvgRenderCacheUtil.SVG_TYPE.UPDATE_STUDIO)

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
            SentryController.captureException(e)

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
            SentryController.captureException(e)

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


    fun shareUncompressedSvg(
            url: String?,
            model: PosterModel?,
            packageName:String?=null
        ) {
        if (url==null||model==null){
            return
        }
        url.let {
            CoroutineScope(Dispatchers.IO).launch {
                val b = svgToBitmap(model)
                shareBitmap(b,model.greeting_message,packageName)
            }
        }

    }

    fun shareUncompressedSvg(
        svgUrl: String?,
        msg:String?=null,
        packageName:String?=null
    ) {
        if (svgUrl==null){
            return
        }

            CoroutineScope(Dispatchers.IO).launch {
                val b = svgToBitmap(svgUrl)
                shareBitmap(b,msg,packageName)
            }


    }

    suspend fun shareBitmap(b:Bitmap?,msg:String?,packageName: String?,){
        withContext(Dispatchers.Default) {
            when (packageName) {
                PackageNames.INSTAGRAM ->{
                    b?.shareAsImage(
                        PackageNames.INSTAGRAM,
                        text = msg
                    )
                }
                PackageNames.WHATSAPP ->{
                    b?.shareAsImage(
                        PackageNames.WHATSAPP,
                        text = RegexUtils.addStarToNumbers(msg)
                    )
                }
                "" -> b?.shareAsImage(text = msg)
                else -> b?.saveImageToStorage(showNoti = true)
            }
        }

    }




    suspend fun svgToBitmap(svg:SVG): Bitmap? {

               /* svg.renderDPI =
                    BaseApplication.instance.resources?.displayMetrics?.densityDpi?.toFloat()
                        ?: 480.0f
                svg.documentWidth = svg.documentWidth * 4
                svg.documentHeight = svg.documentHeight * 4*/
                val b = Bitmap.createBitmap(
                    svg.documentWidth.toInt(),
                    svg.documentHeight.toInt(), Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(b)
                svg.renderToCanvas(canvas)

                return b

    }

    suspend fun svgToBitmap(model:PosterModel): Bitmap? {
        val svgUrl = model.url()!!
        var svgString = FestivePosterSvgRenderCache.instance.retrieveFromCache(svgUrl)
        if (svgString == null || svgString.isEmpty()) {
            svgString = getSvgAsAString(svgUrl)
            svgString?.let { FestivePosterSvgRenderCache.instance.saveToCache(svgUrl, it) }
        }
        if (svgString != null && !svgString.isEmpty()) {
            svgString = FestivePosterSvgRenderCache.instance.replace(
                    svgString,
                    model.keys!!,
                    BaseApplication.instance,
                )

            val svg = SVG.getFromString(svgString)

            return svgToBitmap(svg)
        }

        return null
    }

    suspend fun svgToBitmap(svgUrl:String?): Bitmap? {
        if (svgUrl==null){
            return null
        }
        var svgString = UpdateStudioSvgRenderCache.instance.retrieveFromCache(svgUrl)
        if (svgString == null || svgString.isEmpty()) {
            svgString = getSvgAsAString(svgUrl)
            svgString?.let { UpdateStudioSvgRenderCache.instance.saveToCache(svgUrl, it) }
        }

        if (svgString != null && !svgString.isEmpty()) {
            svgString = UpdateStudioSvgRenderCache.instance.replace(
                svgString,
                application()
            )

            val svg = SVG.getFromString(svgString)

            return svgToBitmap(svg)
        }
        return null

    }
}
package com.framework.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule  //1
class ProgressAppGlideModule : AppGlideModule() {

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) { //2
    super.registerComponents(context, glide, registry)
    val client = OkHttpClient.Builder()
      .addNetworkInterceptor { chain ->
        //3
        val request = chain.request()
        val response = chain.proceed(request)
        val listener = DispatchingProgressManager()  //4
        response.newBuilder()
          .body(
            OkHttpProgressResponseBody(
              request.url,
              response.body!!,
              listener
            )
          )  //5
          .build()
      }
      .build()
    glide.registry.replace(
      GlideUrl::class.java,
      InputStream::class.java,
      OkHttpUrlLoader.Factory(client)
    ) //6
  }
}
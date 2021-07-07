package com.framework.rest

import com.framework.BaseApplication
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class BaseApiClient protected constructor(isAuthRemove: Boolean = false) {

  lateinit var retrofit: Retrofit
  private var httpClient: OkHttpClient.Builder
  private var gson = GsonBuilder().setLenient().create()

  companion object {
    val shared = BaseApiClient()
  }

  init {
    val session = UserSessionManager(BaseApplication.instance)
    val tokenResult = session.getAccessTokenAuth()
    val authInterceptor = ServiceInterceptor(isAuthRemove, tokenResult?.token)

    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    httpClient = OkHttpClient.Builder()
    httpClient.readTimeout(2, TimeUnit.MINUTES)
      .connectTimeout(2, TimeUnit.MINUTES)
      .writeTimeout(2, TimeUnit.MINUTES)
    httpClient.addInterceptor(authInterceptor).addInterceptor(logging)

    gson = GsonBuilder().setLenient().create()
  }

  fun init(baseUrl: String) {
    retrofit = Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
      .client(httpClient.build())
      .build()
  }
}
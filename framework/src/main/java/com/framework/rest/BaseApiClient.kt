package com.framework.rest

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class BaseApiClient protected constructor() {

  lateinit var retrofit: Retrofit
  private var gson: Gson
  private var httpClient: OkHttpClient.Builder

  companion object {
    val shared = BaseApiClient()
  }

  init {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)

    val interceptors = getInterceptors()
    for (interceptor in interceptors) {
      httpClient.addInterceptor(interceptor)
    }

    gson = GsonBuilder().create()

  }

  protected fun getInterceptors(): ArrayList<Interceptor> {
    return ArrayList()
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
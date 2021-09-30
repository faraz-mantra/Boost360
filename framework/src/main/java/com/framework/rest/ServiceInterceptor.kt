package com.framework.rest

import android.util.Log
import com.framework.BaseApplication
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

class ServiceInterceptor(var isAuthRemove: Boolean) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()
    val session = UserSessionManager(BaseApplication.instance)
    val tokenResult = session.getAccessTokenAuth()
    if (isAuthRemove.not() && tokenResult?.token.isNullOrEmpty().not()) {
      request = request.newBuilder().addHeader("Authorization", "Bearer ${tokenResult?.token}").build()
    }
    val response = chain.proceed(request)
    val rawJson = response.body?.string()
    Log.i("ServiceInterceptor", "request: "+Gson().toJson(request))

    Log.i("ServiceInterceptor", "response: "+Gson().toJson(rawJson))

    return response.newBuilder()
      .body(rawJson?.toResponseBody()).build();
  }
}
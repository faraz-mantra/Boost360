package com.framework.rest

import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor(var isAuthRemove: Boolean, var token: String?) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()
    if (isAuthRemove.not() && token.isNullOrEmpty().not()) {
      request = request.newBuilder().addHeader("Authorization", "Bearer $token").build()
    }
    return chain.proceed(request)
  }
}
package com.framework.rest

import com.framework.BaseApplication
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.UUIDUtils
import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor(var isAuthRemove: Boolean) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()
    if (isAuthRemove.not()) {
      val session = UserSessionManager(BaseApplication.instance)
      val tokenResult = session.getAccessTokenAuth()
      request = request.newBuilder().addHeader("Authorization", "Bearer ${tokenResult?.token}").build()
    }
    request = request.newBuilder().addHeader("X-Correlation-Id", UUIDUtils.generateRandomUUID()).build()
    return chain.proceed(request)
  }
}
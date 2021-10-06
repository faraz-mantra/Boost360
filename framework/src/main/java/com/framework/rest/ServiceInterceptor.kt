package com.framework.rest

import com.auth0.android.jwt.JWT
import com.framework.BaseApplication
import com.framework.pref.*
import com.framework.rest.tokenCreate.AccessTokenRequest
import com.framework.rest.tokenCreate.EndPoints
import com.framework.rest.tokenCreate.RefreshTokenApi
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceInterceptor(var isAuthRemove: Boolean) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return runBlocking {
      var request = chain.request()
      val session = UserSessionManager(BaseApplication.instance)
      var tokenResult = session.getAccessTokenAuth()
      if (isAuthRemove.not() && tokenResult?.token.isNullOrEmpty().not()) {
        val isExpired = JWT(tokenResult?.token ?: "").isExpired(10)
        if (isExpired) tokenResult = session.getToken(tokenResult)
        request = request.newBuilder().addHeader("Authorization", "Bearer ${tokenResult?.token}").build()
      }
      chain.proceed(request)
    }
  }


  private suspend fun UserSessionManager.getToken(tokenResult: TokenResult?): TokenResult? {
    val response = clientApi().createAccessToken(AccessTokenRequest(tokenResult?.refreshToken, clientId, this.fPID))
    return if (response.isSuccessful && response.body()?.result?.token.isNullOrEmpty().not()) {
      val result = response.body()?.result
      if (result?.refreshToken.isNullOrEmpty().not()) tokenResult?.refreshToken = result?.refreshToken
      tokenResult?.token = result?.token
      this.saveAccessTokenAuth(tokenResult)
      tokenResult
    } else{
      tokenResult
    }
  }

  private fun clientApi(): RefreshTokenApi {
    val okHttpClient = OkHttpClient().newBuilder().readTimeout(2, TimeUnit.MINUTES)
      .connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES)
      .addInterceptor(HttpLoggingInterceptor().apply { HttpLoggingInterceptor.Level.BODY }).build()
    val retrofit = Retrofit.Builder().baseUrl(EndPoints.WITH_FLOAT_API_TWO).client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).build()
    return retrofit.create(RefreshTokenApi::class.java)
  }
}
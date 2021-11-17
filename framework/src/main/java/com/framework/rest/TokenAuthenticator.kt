package com.framework.rest

import android.util.Log
import com.framework.BaseApplication
import com.framework.pref.*
import com.framework.rest.tokenCreate.AccessTokenRequest
import com.framework.rest.tokenCreate.EndPoints
import com.framework.rest.tokenCreate.RefreshTokenApi
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenAuthenticator(var isAuthRemove: Boolean) : Authenticator {

  override fun authenticate(route: Route?, response: Response): Request? {
    return if (isAuthRemove.not()) {
      Log.d("authenticate", "Run")
      val session = UserSessionManager(BaseApplication.instance)
      val tokenResult = runBlocking { session.getToken(session.getAccessTokenAuth()) }
      return if (tokenResult == null) {
        Log.d("authenticate","empty")
        // Refresh token failed, you can logout user or retry couple of times
        // Returning null is critical here, it will stop the current request
        // If you do not return null, you will end up in a loop calling refresh
        session.logoutUser(BaseApplication.instance.applicationContext)
        null
      } else {
        Log.d("authenticate","token: ${tokenResult.token}")
        response.request.newBuilder().header("Authorization", "Bearer ${tokenResult.token}").build()
      }
    } else response.request
  }

  private suspend fun UserSessionManager.getToken(tokenResult: TokenResult?): TokenResult? {
    val response = clientApi().createAccessToken(AccessTokenRequest(tokenResult?.refreshToken, clientId, this.fPID))
    return if (response.isSuccessful && response.body()?.result?.token.isNullOrEmpty().not()) {
      val result = response.body()?.result
      if (result?.refreshToken.isNullOrEmpty().not()) tokenResult?.refreshToken = result?.refreshToken
      tokenResult?.token = result?.token
      this.saveAccessTokenAuth(tokenResult)
      tokenResult
    } else {
      null
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
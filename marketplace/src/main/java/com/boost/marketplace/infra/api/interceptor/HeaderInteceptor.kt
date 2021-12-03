package com.boost.marketplace.infra.api.interceptor

import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.infra.utils.LogUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(sharedPrefs: SharedPrefs) : Interceptor {
    private val sharedPrefs: SharedPrefs

    companion object {
        val TAG = this::class.java.simpleName
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder: Request.Builder = chain.request().newBuilder()
//        requestBuilder.header("Authorization", sharedPrefs.getAuthToken()!!)
        try {
//            requestBuilder.header("Access-Control-Allow-Origin", BuildConfig.DOMAIN)
        } catch (e: Exception) {
            LogUtils.error(TAG, e.localizedMessage)
        }
        requestBuilder.header("Cache-Control", "no-store")
        requestBuilder.header("X-Content-Type-Options", "nosniff")
        return chain.proceed(requestBuilder.build())
    }

    init {
        this.sharedPrefs = sharedPrefs
    }
}
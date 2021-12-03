package com.boost.marketplace.infra.di

import androidx.annotation.NonNull
import com.boost.dbcenterapi.utils.Constants
import com.boost.marketplace.BuildConfig
import com.boost.marketplace.infra.api.MarketplaceApiService
import com.boost.marketplace.infra.api.interceptor.ErrorHandlingInterceptor
import com.boost.marketplace.infra.api.interceptor.HeaderInterceptor
import com.boost.marketplace.infra.utils.LogUtils.error
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {
    companion object {
        val TAG = this::class.java.simpleName
    }

    @Singleton
    @Provides
    fun provideApiService(headerInterceptor: HeaderInterceptor,
                          errorHandlingInterceptor: ErrorHandlingInterceptor
    ): MarketplaceApiService {

        val client: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(errorHandlingInterceptor)
            .followRedirects(false)
            .followSslRedirects(false)
            .cache(null)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MarketplaceApiService::class.java)
    }

    private val httpLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            return loggingInterceptor
        }
}


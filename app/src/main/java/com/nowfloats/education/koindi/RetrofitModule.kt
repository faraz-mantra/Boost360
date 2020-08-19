package com.nowfloats.education.koindi

import android.util.Log
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.service.IEducationService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val retrofitModule = module {


    single {
        okHttp()
    }
    single {
        retrofit(Constants.EDUCATION_API_BASE_URL)
    }
    single {
        get<Retrofit>().create(IEducationService::class.java)
    }

}

fun okHttp() = OkHttpClient.Builder()
        .build()

fun retrofit(baseUrl: String) = Retrofit.Builder()
        .callFactory(OkHttpClient.Builder().build())
        .baseUrl(baseUrl)
        .addConverterFactory(gson())
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient(okhttpIntersepter(), loggingInterceptor()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

private fun gson(): retrofit2.converter.gson.GsonConverterFactory =
        retrofit2.converter.gson.GsonConverterFactory.create(
                com.google.gson.GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        )

fun okHttpClient(
        interceptor: okhttp3.Interceptor,
        loggingInterceptor: okhttp3.logging.HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
            .readTimeout(Constants.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(Constants.CONNECTION_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
}

private fun okhttpIntersepter(): okhttp3.Interceptor {
    return okhttp3.Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        chain.proceed(requestBuilder.build())
    }
}

private fun loggingInterceptor(): okhttp3.logging.HttpLoggingInterceptor {
    val interceptor =
            okhttp3.logging.HttpLoggingInterceptor(okhttp3.logging.HttpLoggingInterceptor.Logger { message ->
                Log.i("KoinRetrofitModule", message)
            })
    interceptor.level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
    return interceptor
}
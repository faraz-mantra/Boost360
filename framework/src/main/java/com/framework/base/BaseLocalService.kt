package com.framework.base

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import io.reactivex.Observable
import java.io.BufferedReader
import java.io.InputStreamReader

open class BaseLocalService {

  fun <T : BaseResponse> fromJsonRes(context: Context, @RawRes id: Int, classOfT: Class<T>): Observable<BaseResponse> {
    return try {
      Observable.just(Gson().fromJson(getLocalJsonReader(context, id), classOfT).apply { status = 200 })
    } catch (e: Exception) {
      Observable.just(BaseResponse().apply { status = 404;error = e })
    }
  }

  fun <T : BaseResponse> fromJsonResNew(context: Context, @RawRes id: Int, classOfT: Class<T>): BaseResponse {
    return try {
      Gson().fromJson(getLocalJsonReader(context, id), classOfT).apply { status = 200 }
    } catch (e: Exception) {
      BaseResponse().apply { status = 404;error = e }
    }
  }

  private fun getLocalJsonReader(context: Context, @RawRes id: Int): BufferedReader {
    return BufferedReader(InputStreamReader(context.resources.openRawResource(id)))
  }

  fun saveToLocal(response: BaseResponse, taskcode: Int) {

  }
}
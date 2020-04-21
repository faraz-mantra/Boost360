package com.framework.base

import com.framework.exceptions.BaseException
import com.framework.exceptions.NoNetworkException
import com.framework.utils.NetworkUtils
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

abstract class BaseRepository<RemoteDataSource, LocalDataSource : BaseLocalService> {

  protected val remoteDataSource: RemoteDataSource
    get() = getApiClient().create(getRemoteDataSourceClass())

  protected val localDataSource: LocalDataSource
    get() = getLocalDataSourceInstance()

  protected abstract fun getRemoteDataSourceClass(): Class<RemoteDataSource>
  protected abstract fun getLocalDataSourceInstance(): LocalDataSource

  protected abstract fun getApiClient(): Retrofit

  fun <T> makeRemoteRequest(observable: Observable<Response<T>>, taskcode: Int): Observable<BaseResponse> {
    if (!NetworkUtils.isNetworkConnected()) {
      val response = BaseResponse(error = NoNetworkException())
      return Observable.just(response)
    }

    return observable.map {
      if (it.isSuccessful) {
        val response = when (it.body()) {
          is Array<*> -> BaseResponse(message = "Success", arrayResponse = it.body() as Array<*>)
          is String -> BaseResponse(message = "Success", stringResponse = it.body() as String)
          is Objects -> it.body() as BaseResponse
          else -> BaseResponse(message = "Success")
        }
        response.status = it.code()
        response.taskcode = taskcode
        onSuccess(response, taskcode)
        return@map response
      } else {
        val response = BaseResponse()
        response.status = it.code()
        response.error = BaseException(it.errorBody()?.string() ?: "")
        response.message = response.error?.localizedMessage
        response.taskcode = taskcode
        onFailure(response, taskcode)
        return@map response
      }
    }.onErrorReturn {
      it.printStackTrace()
      val response = BaseResponse()
      response.error = it
      response.message = it.localizedMessage
      response.taskcode = taskcode
      onFailure(response, taskcode)
      response
    }
  }

  fun makeLocalResponse(observable: Observable<BaseResponse>, taskcode: Int): Observable<BaseResponse> {
    return observable.map {
      if (it.error != null) {
        onFailure(it, taskcode)
        return@map it
      } else {
        onSuccess(it, taskcode)
        return@map it
      }
    }
  }

  protected fun onFailure(response: BaseResponse, taskcode: Int) {
    if (response.error == null) response.error = Exception()
  }

  protected fun onSuccess(response: BaseResponse, taskcode: Int) {

  }
}



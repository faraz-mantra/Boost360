package com.framework.base

import com.framework.exceptions.BaseException
import com.framework.exceptions.NoNetworkException
import com.framework.utils.NetworkUtils
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

abstract class BaseRepository<RemoteDataSource, LocalDataSource : BaseLocalService> {

  protected val remoteDataSource: RemoteDataSource
    get() = getApiClient().create(getRemoteDataSourceClass())

  protected val localDataSource: LocalDataSource
    get() = getLocalDataSourceInstance()

  protected abstract fun getRemoteDataSourceClass(): Class<RemoteDataSource>
  protected abstract fun getLocalDataSourceInstance(): LocalDataSource

  protected abstract fun getApiClient(): Retrofit

  fun <T> makeRemoteRequest(observable: Observable<Response<T>>, taskCode: Int): Observable<BaseResponse> {
    if (!NetworkUtils.isNetworkConnected()) {
      val response = BaseResponse(error = NoNetworkException(), status = 400, message = "No internet connection.")
      return Observable.just(response)
    }

    return observable.map {
      if (it.isSuccessful) {
        val response = when (it.body()) {
          is Array<*> -> BaseResponse(message = "Success", arrayResponse = it.body() as Array<*>)
          is String -> BaseResponse(message = "Success", stringResponse = it.body() as String)
          is BaseResponse -> (it.body() as T) as BaseResponse
          is ResponseBody -> BaseResponse(responseBody = (it.body() as? ResponseBody), message = "Success")
          else -> BaseResponse(anyResponse = it.body(), message = "Success")
        }
        response.status = it.code()
        response.taskcode = taskCode
        onSuccess(response, taskCode)
        return@map response
      } else {
        val response = BaseResponse()
        response.status = it.code()
        response.error = BaseException(it.errorBody()?.string() ?: "")
        response.message = response.error?.localizedMessage
        response.taskcode = taskCode
        onFailure(response, taskCode)
        return@map response
      }
    }.onErrorReturn {
      it.printStackTrace()
      val response = BaseResponse()
      response.error = it
      response.message = it.localizedMessage
      response.taskcode = taskCode
      onFailure(response, taskCode)
      response
    }
  }

  fun makeLocalResponse(observable: Observable<BaseResponse>, taskcode: Int): Observable<BaseResponse> {
    return observable.map {
      if (it.error != null) {
        it.status = 400
        it.taskcode = taskcode
        onFailure(it, taskcode)
        return@map it
      } else {
        it.status = 200
        it.taskcode = taskcode
        onSuccess(it, taskcode)
        return@map it
      }
    }
  }

  open fun onFailure(response: BaseResponse, taskcode: Int) {
    if (response.error == null) response.error = Exception()
  }

  open fun onSuccess(response: BaseResponse, taskcode: Int) {

  }
}



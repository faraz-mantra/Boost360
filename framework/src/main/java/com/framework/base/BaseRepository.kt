package com.framework.base

import io.reactivex.Observable
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

  fun <T : BaseResponse> makeRemoteRequest(
          observable: Observable<Response<T>>, taskcode: Int
  ): Observable<BaseResponse> {
    return observable.map {
      if (it.isSuccessful) {
        val response = it.body() ?: BaseResponse()
        response.status = it.code()
        onSuccess(response, taskcode)
        return@map response
      } else {
        val response = BaseResponse()
        response.status = it.code()
        response.message = it.message()
//                response.error = it.errorBody()
        onFailure(response, taskcode)
        return@map response
      }
    }.onErrorReturn {
      it.printStackTrace()
      val response = BaseResponse()
      response.error = it
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
    response.error = Exception()
  }

  protected fun onSuccess(response: BaseResponse, taskcode: Int) {

  }
}
package com.inventoryorder.base.rest

import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.inventoryorder.rest.Taskcode
import com.inventoryorder.rest.apiClients.WithFloatsApiClient
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit

abstract class AppBaseRepository<RemoteDataSource, LocalDataSource : AppBaseLocalService> :
    BaseRepository<RemoteDataSource, LocalDataSource>() {

  protected fun <T> makeRemoteRequest(observable: Observable<Response<T>>, taskCode: Taskcode): Observable<BaseResponse> {
    return makeRemoteRequest(observable, taskCode.ordinal)
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }

  fun makeLocalRequest(observable: Observable<BaseResponse>, taskCode: Taskcode): Observable<BaseResponse> {
    return makeLocalResponse(observable, taskCode.ordinal)
  }

  protected fun onFailure(response: BaseResponse, taskCode: Taskcode) {
    super.onFailure(response, taskCode.ordinal)
  }

  protected fun onSuccess(response: BaseResponse, taskCode: Taskcode) {
    super.onSuccess(response, taskCode.ordinal)
    localDataSource.saveToLocal(response, taskCode)
  }
}
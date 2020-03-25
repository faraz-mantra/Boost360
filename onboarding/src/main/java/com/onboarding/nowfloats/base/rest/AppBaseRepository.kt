package com.onboarding.nowfloats.base.rest

import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.rest.ApiClient
import com.onboarding.nowfloats.rest.Taskcode
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit

abstract class AppBaseRepository<RemoteDataSource, LocalDataSource : AppBaseLocalService> :
        BaseRepository<RemoteDataSource, LocalDataSource>() {

  protected fun <T : BaseResponse> makeRemoteRequest(
          observable: Observable<Response<T>>, taskcode: Taskcode
  ): Observable<BaseResponse> {
    return makeRemoteRequest(observable, taskcode.ordinal)
  }

  override fun getApiClient(): Retrofit {
    return ApiClient.shared.retrofit
  }

  fun makeLocalRequest(
          observable: Observable<BaseResponse>,
          taskcode: Taskcode
  ): Observable<BaseResponse> {
    return makeLocalResponse(observable, taskcode.ordinal)
  }

  protected fun onFailure(response: BaseResponse, taskcode: Taskcode) {
    super.onFailure(response, taskcode.ordinal)
  }

  protected fun onSuccess(response: BaseResponse, taskcode: Taskcode) {
    super.onSuccess(response, taskcode.ordinal)
    localDataSource.saveToLocal(response, taskcode)
  }
}
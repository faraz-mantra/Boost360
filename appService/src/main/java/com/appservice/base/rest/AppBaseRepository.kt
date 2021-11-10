package com.appservice.base.rest

import com.appservice.AppServiceApplication
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.WithFloatsApiTwoClient
import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.framework.pref.UserSessionManager
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit

abstract class AppBaseRepository<RemoteDataSource, LocalDataSource : AppBaseLocalService> : BaseRepository<RemoteDataSource, LocalDataSource>() {

  protected fun <T> makeRemoteRequest(observable: Observable<Response<T>>, taskCode: TaskCode): Observable<BaseResponse> {
    return makeRemoteRequest(observable, taskCode.ordinal)
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiTwoClient.shared.retrofit
  }

  fun makeLocalRequest(observable: Observable<BaseResponse>, taskCode: TaskCode): Observable<BaseResponse> {
    return makeLocalResponse(observable, taskCode.ordinal)
  }

  override fun onFailure(response: BaseResponse, taskCode: Int) {
    super.onFailure(response, taskCode)
    unauthorizedUserCheck(response.status)
  }

  override fun onSuccess(response: BaseResponse, taskCode: Int) {
    super.onSuccess(response, taskCode)
    unauthorizedUserCheck(response.status)
  }

  private fun unauthorizedUserCheck(taskCode: Int?) {
    if (taskCode == 401) {
      AppServiceApplication.instance.apply {
        UserSessionManager(this).logoutUser(this, true)
      }
    }
  }
}
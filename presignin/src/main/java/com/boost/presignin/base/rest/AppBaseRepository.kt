package com.boost.presignin.base.rest

import com.boost.presignin.AppPreSignInApplication
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
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
    return WithFloatsApiClient.shared.retrofit
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

  open fun unauthorizedUserCheck(taskCode: Int?) {
    if (taskCode == 401) {
      AppPreSignInApplication.instance.apply {
        UserSessionManager(this).logoutUser(this, true)
      }
    }
  }
}
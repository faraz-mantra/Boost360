package com.inventoryorder.base.rest

import android.content.Intent
import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.inventoryorder.BaseOrderApplication
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.WithFloatsApiClient
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit

abstract class AppBaseRepository<RemoteDataSource, LocalDataSource : AppBaseLocalService> : BaseRepository<RemoteDataSource, LocalDataSource>() {

  protected fun <T> makeRemoteRequest(observable: Observable<Response<T>>, taskCode: TaskCode): Observable<BaseResponse> {
    return makeRemoteRequest(observable, taskCode.ordinal)
  }

  fun makeLocalRequest(observable: Observable<BaseResponse>, taskCode: TaskCode): Observable<BaseResponse> {
    return makeLocalResponse(observable, taskCode.ordinal)
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }

  override fun onFailure(response: BaseResponse, taskcode: Int) {
    super.onFailure(response, taskcode)
    unauthorizedUserCheck(taskcode)
  }

  override fun onSuccess(response: BaseResponse, taskcode: Int) {
    super.onSuccess(response, taskcode)
    unauthorizedUserCheck(taskcode)
  }

  private fun unauthorizedUserCheck(taskCode: Int) {
    if (taskCode == 401) {
      BaseOrderApplication.instance.apply {
        try {
          val i = Intent(this, Class.forName("com.nowfloats.helper.LogoutActivity"))
          startActivity(i)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }
}
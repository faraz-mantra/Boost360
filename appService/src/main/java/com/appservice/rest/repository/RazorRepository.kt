package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.RazorApiClient
import com.appservice.rest.services.RazorRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object RazorRepository : AppBaseRepository<RazorRemoteData, AppBaseLocalService>() {

  fun ifscDetail(ifsc: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.ifscDetail(ifsc), TaskCode.IFSC_DETAIL)
  }

  override fun getRemoteDataSourceClass(): Class<RazorRemoteData> {
    return RazorRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return RazorApiClient.shared.retrofit
  }
}

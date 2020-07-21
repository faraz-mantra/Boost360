package com.catlogservice.rest.repository

import com.catlogservice.base.rest.AppBaseLocalService
import com.catlogservice.base.rest.AppBaseRepository
import com.catlogservice.rest.TaskCode
import com.catlogservice.rest.apiClients.RazorApiClient
import com.catlogservice.rest.services.RazorRemoteData
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

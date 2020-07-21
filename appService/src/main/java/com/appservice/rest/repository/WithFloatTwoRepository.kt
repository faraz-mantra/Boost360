package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.WithFloatsApiTwoClient
import com.appservice.rest.services.WithFloatTwoRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, AppBaseLocalService>() {

  fun createService(request: Any): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createService(request), TaskCode.POST_CREATE_SERVICE)
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatTwoRemoteData> {
    return WithFloatTwoRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiTwoClient.shared.retrofit
  }
}

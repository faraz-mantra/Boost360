package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.AssuredWithFloatsApiClient
import com.appservice.rest.services.AssuredWithFloatRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object AssuredWithFloatRepository :
  AppBaseRepository<AssuredWithFloatRemoteData, AppBaseLocalService>() {

  fun getPickUpAddress(fpId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getPickUpAddress(fpId), TaskCode.PIC_UP_ADDRESS)
  }


  override fun getRemoteDataSourceClass(): Class<AssuredWithFloatRemoteData> {
    return AssuredWithFloatRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return AssuredWithFloatsApiClient.shared.retrofit
  }
}

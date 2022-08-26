package com.festive.poster.reset.repo

import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.AzureWebsiteNetApiClient
import com.festive.poster.reset.services.AzureWebsiteKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object AzureWebsiteNewRepository : AppBaseRepository<AzureWebsiteKitRemoteData, AppBaseLocalService>() {



  fun getFeatureDetails(): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFeatureDetails(session.fPID), TaskCode.GET_FEATURE_DETAILS)
  }


  override fun getRemoteDataSourceClass(): Class<AzureWebsiteKitRemoteData> {
    return AzureWebsiteKitRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return AzureWebsiteNetApiClient.shared.retrofit
  }
}
package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.AzureWebsiteNetApiClient
import com.appservice.rest.services.AzureWebsiteKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit


object AzureWebsiteNewRepository : AppBaseRepository<AzureWebsiteKitRemoteData, AppBaseLocalService>() {


  fun getCapLimitFeatureDetails(fpId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getCapLimitFeatureDetails(fpId,clientId), TaskCode.CAP_LIMIT_FEATURE_DETAILS)
  }

  fun getFeatureDetails(fpId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFeatureDetails(fpId,clientId), TaskCode.GET_FEATURE_DETAILS)
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

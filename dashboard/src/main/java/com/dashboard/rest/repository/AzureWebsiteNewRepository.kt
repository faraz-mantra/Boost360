package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.model.websitetheme.WebsiteThemeUpdateRequest
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.AzureWebsiteNetApiClient
import com.dashboard.rest.apiClients.DevBoostKitApiClient
import com.dashboard.rest.services.AzureWebsiteKitRemoteData
import com.dashboard.rest.services.DevBoostKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.Query


object AzureWebsiteNewRepository : AppBaseRepository<AzureWebsiteKitRemoteData, AppBaseLocalService>() {


  fun getCapLimitFeatureDetails(fpId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getCapLimitFeatureDetails(fpId,clientId), TaskCode.CAP_LIMIT_FEATURE_DETAILS)
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

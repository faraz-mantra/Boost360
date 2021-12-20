package com.boost.dbcenterapi.data.rest.repository

import com.boost.dbcenterapi.base.rest.AppBaseLocalService
import com.boost.dbcenterapi.base.rest.AppBaseRepository
import com.boost.dbcenterapi.data.rest.TaskCode
import com.boost.dbcenterapi.data.rest.apiClients.DeveloperBoostKitApiClient
import com.boost.dbcenterapi.data.rest.services.MarketplaceApiService
import com.boost.dbcenterapi.data.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

const val AUTHORIZATION = "591c0972ee786cbf48bd86cf"


object DeveloperBoostKitRepository : AppBaseRepository<MarketplaceApiService, AppBaseLocalService>() {


  fun getAllMarketPlaceData(
    auth: String = AUTHORIZATION,
    website_id: String?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getAllFeatures(auth, website_id),
      TaskCode.GET_ALL_FEATURES
    )
  }

  override fun getRemoteDataSourceClass(): Class<MarketplaceApiService> {
    return MarketplaceApiService::class.java

  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return DeveloperBoostKitApiClient.shared.retrofit
  }
}
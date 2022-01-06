package com.boost.dbcenterapi.data.rest.repository

import com.boost.dbcenterapi.base.rest.AppBaseLocalService
import com.boost.dbcenterapi.base.rest.AppBaseRepository
import com.boost.dbcenterapi.data.rest.TaskCode
import com.boost.dbcenterapi.data.rest.apiClients.MarketplaceNewApiClient
import com.boost.dbcenterapi.data.rest.services.MarketplaceNewApiInterface
import com.boost.dbcenterapi.data.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object MarketplaceNewRepository : AppBaseRepository<MarketplaceNewApiInterface, AppBaseLocalService>() {

  fun getAllFeatures(): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.GetAllFeatures(),
      TaskCode.GET_ALL_FEATURES
    )
  }

  override fun getRemoteDataSourceClass(): Class<MarketplaceNewApiInterface> {
    return MarketplaceNewApiInterface::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return MarketplaceNewApiClient.shared.retrofit
  }
}
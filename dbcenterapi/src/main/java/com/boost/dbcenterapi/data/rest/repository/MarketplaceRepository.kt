package com.boost.dbcenterapi.data.rest.repository

import com.boost.dbcenterapi.base.rest.AppBaseLocalService
import com.boost.dbcenterapi.base.rest.AppBaseRepository
import com.boost.dbcenterapi.data.rest.TaskCode
import com.boost.dbcenterapi.data.rest.apiClients.MarketplaceApiClient
import com.boost.dbcenterapi.data.rest.apiClients.MarketplaceNewApiClient
import com.boost.dbcenterapi.data.rest.services.MarketplaceApiInterface
import com.boost.dbcenterapi.data.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object MarketplaceRepository : AppBaseRepository<MarketplaceApiInterface, AppBaseLocalService>() {

//  fun getAllFeatures(): Observable<BaseResponse> {
//    return makeRemoteRequest(
//      remoteDataSource.CreatePurchaseAutoRenewOrder(),
//      TaskCode.GET_ALL_FEATURES
//    )
//  }


  fun GetFloatingPointWebWidgets(fpid: String, clientId: String): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.GetFloatingPointWebWidgets(fpid,clientId),
      TaskCode.GET_FLOATING_POINT_WIDGETS
    )
  }

  override fun getRemoteDataSourceClass(): Class<MarketplaceApiInterface> {
    return MarketplaceApiInterface::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return MarketplaceApiClient.shared.retrofit
  }
}
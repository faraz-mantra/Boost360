package com.boost.dbcenterapi.data.rest.repository

import com.boost.dbcenterapi.base.rest.AppBaseRepository
import com.boost.dbcenterapi.data.rest.TaskCode
import com.boost.dbcenterapi.data.rest.apiClients.WithFloatsApiClient
import com.boost.dbcenterapi.data.rest.services.MarketplaceApiService
import com.boost.dbcenterapi.data.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object WithFloatsRepository : AppBaseRepository<MarketplaceApiService, DashboardLocalDataSource>(){

  fun getFpWidgets(auth:String,fpId:String,clientId:String):Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.getFloatingPointWebWidgets(auth,fpId,clientId),TaskCode.GET_FLOATING_POINT_WIDGETS)
  }
  override fun getRemoteDataSourceClass(): Class<MarketplaceApiService> {
    return MarketplaceApiService::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}
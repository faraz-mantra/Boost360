package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.PluginFloatsApiClient
import com.dashboard.rest.services.PluginFloatRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object PluginFloatRepository : AppBaseRepository<PluginFloatRemoteData, AppBaseLocalService>() {

  fun  getDomainDetailsForFloatingPoint(fpTag: String?,map: Map<String, String>?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getDomainDetailsForFloatingPoint(fpTag,map), TaskCode.GET_DOMAIN_DETAIL)
  }

  override fun getRemoteDataSourceClass(): Class<PluginFloatRemoteData> {
    return PluginFloatRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return PluginFloatsApiClient.shared.retrofit
  }
}

package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.DevBoostKitNewApiClient
import com.dashboard.rest.services.DevBoostKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object DevBoostKitNewRepository : AppBaseRepository<DevBoostKitRemoteData, AppBaseLocalService>() {

  fun getSearchAnalytics(website_id: String?, startDate: String?, endDate: String?,auth: String = DEVELOPER_ID): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getSearchAnalytics(auth,website_id, startDate, endDate), TaskCode.GET_SEARCH_ANALYTICS)
  }

  override fun getRemoteDataSourceClass(): Class<DevBoostKitRemoteData> {
    return DevBoostKitRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return DevBoostKitNewApiClient.shared.retrofit
  }
}

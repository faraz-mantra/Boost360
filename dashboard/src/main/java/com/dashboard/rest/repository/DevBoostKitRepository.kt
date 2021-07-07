package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.DevBoostKitApiClient
import com.dashboard.rest.services.DevBoostKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

const val DEVELOPER_ID = "591c0972ee786cbf48bd86cf"

object DevBoostKitRepository : AppBaseRepository<DevBoostKitRemoteData, AppBaseLocalService>() {

  fun getUpgradePremiumBanner(
    auth: String = DEVELOPER_ID,
    website_id: String?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getUpgradePremiumBanner(auth, website_id),
      TaskCode.GET_UPGRADE_PREMIUM_BANNER
    )
  }

  fun getUpgradeDashboardBanner(
    auth: String = DEVELOPER_ID,
    website_id: String?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getUpgradeDashboardBanner(auth, website_id),
      TaskCode.GET_UPGRADE_DASHBOARD_BANNER
    )
  }

  fun getSearchAnalytics(
    website_id: String?,
    startDate: String?,
    endDate: String?,
    auth: String = DEVELOPER_ID
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSearchAnalytics(
        auth,
        website_id,
        startDate,
        endDate
      ), TaskCode.GET_SEARCH_ANALYTICS
    )
  }


  override fun getRemoteDataSourceClass(): Class<DevBoostKitRemoteData> {
    return DevBoostKitRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return DevBoostKitApiClient.shared.retrofit
  }
}

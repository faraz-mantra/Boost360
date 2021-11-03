package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.model.DisableBadgeNotificationRequest
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.UsCentralNowFloatsCloudApiClient
import com.dashboard.rest.services.UsCentralNowFloatsCloudRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object UsCentralNowFloatsCloudRepository : AppBaseRepository<UsCentralNowFloatsCloudRemoteData, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<UsCentralNowFloatsCloudRemoteData> {
    return UsCentralNowFloatsCloudRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return UsCentralNowFloatsCloudApiClient.shared.retrofit
  }

  fun disableBadgeNotification(request: DisableBadgeNotificationRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.disableBadgeNotification(request), TaskCode.DISABLE_BADGE_NOTIFICATION)
  }
}
package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.model.websitetheme.WebsiteThemeUpdateRequest
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.NowFloatsApiClient
import com.dashboard.rest.services.NowFloatsRemoteData
import com.dashboard.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit


object NowFloatsRepository : AppBaseRepository<NowFloatsRemoteData, AppBaseLocalService>() {

  fun getWebsiteCustomTheme(floatingPointId: String): Observable<BaseResponse> {
    return NowFloatsRepository.makeRemoteRequest(remoteDataSource.getWebsiteTheme(floatingPointId), TaskCode.GET_WEBSITE_CUSTOM_THEME)
  }
  fun updateWebsiteTheme(request: WebsiteThemeUpdateRequest): Observable<BaseResponse> {
    return NowFloatsRepository.makeRemoteRequest(remoteDataSource.updateWebsiteTheme(request), TaskCode.POST_UPDATE_WEBSITE_THEME)
  }

  override fun getRemoteDataSourceClass(): Class<NowFloatsRemoteData> {
    return NowFloatsRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return NowFloatsApiClient.shared.retrofit
  }
}

package com.dashboard.rest.services

import com.dashboard.model.live.dashboardBanner.DashboardPremiumBannerResponse
import com.dashboard.model.live.premiumBanner.UpgradePremiumFeatureResponse
import com.dashboard.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DevBoostKitRemoteData {

  @GET(EndPoints.UPGRADE_PREMIUM_BANNER)
  fun getUpgradePremiumBanner(
      @Header("Authorization") auth: String,
      @Query("website") website_id: String?,
  ): Observable<Response<UpgradePremiumFeatureResponse>>

  @GET(EndPoints.UPGRADE_DASHBOARD_BANNER)
  fun getUpgradeDashboardBanner(
      @Header("Authorization") auth: String,
      @Query("website") website_id: String?,
  ): Observable<Response<DashboardPremiumBannerResponse>>

  @GET(EndPoints.SEARCH_ANALYTICS)
  fun getSearchAnalytics(
      @Header("Authorization") auth: String,
      @Query("websiteId") website_id: String?,
      @Query("startDate") startDate: String?,
      @Query("endDate") endDate: String?,
  ): Observable<Response<Any>>
}
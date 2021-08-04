package com.dashboard.rest.services

import com.dashboard.model.live.caplimit_feature.CapLimitFeatureResponseItem
import com.dashboard.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AzureWebsiteKitRemoteData {

  @GET(EndPoints.GET_FEATURE_DETAILS)
  fun getCapLimitFeatureDetails(
    @Query("fpId") fpId: String?,
    @Query("clientId") clientId: String?,
  ): Observable<Response<Array<CapLimitFeatureResponseItem>>>
}
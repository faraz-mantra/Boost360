package com.appservice.rest.services

import com.appservice.rest.EndPoints
import com.framework.models.caplimit_feature.CapLimitFeatureResponseItem
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

  @GET(EndPoints.GET_FEATURE_DETAILS_2)
  fun getFeatureDetails(
    @Query("fpId") fpId: String?,
    @Query("clientId") clientId: String?,
  ): Observable<Response<Array<CapLimitFeatureResponseItem>>>
}
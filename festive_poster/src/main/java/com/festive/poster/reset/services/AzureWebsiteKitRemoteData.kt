package com.festive.poster.reset.services

import com.festive.poster.reset.EndPoints
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.pref.clientId1
import com.framework.pref.clientId2
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AzureWebsiteKitRemoteData {



  @GET(EndPoints.GET_FEATURE_DETAILS_2)
  fun getFeatureDetails(
    @Query("fpId") fpId: String?,
    @Query("clientId") clientId: String= clientId2,
  ): Observable<Response<Array<CapLimitFeatureResponseItem>>>
}
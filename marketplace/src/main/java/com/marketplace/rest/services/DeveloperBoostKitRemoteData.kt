package com.marketplace.rest.services

import com.marketplace.model.features.FeatureResponse
import com.marketplace.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DeveloperBoostKitRemoteData {
    @GET(EndPoints.GET_ALL_FEATURES)
    fun getAllFeatures(
        @Header("Authorization") auth: String?,
        @Query("website") website: String?,
    ): Observable<Response<FeatureResponse>>


}
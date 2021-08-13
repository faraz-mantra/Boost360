package com.marketplace.rest.services

import com.marketplace.model.features.FeatureResponse
import com.marketplace.rest.EndPoints
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DeveloperBoostKitRemoteData {
    @GET(EndPoints.GET_ALL_FEATURES)
    fun getAllFeatures(
        @Header("Authorization") auth: String?,
        @Query("website") website: String?,
    ): Observable<Response<FeatureResponse>>


}
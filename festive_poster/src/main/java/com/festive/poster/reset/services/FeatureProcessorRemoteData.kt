package com.festive.poster.reset.services

import com.festive.poster.models.GetFeatureDetailsItem
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface FeatureProcessorRemoteData {

    @GET(EndPoints.GET_FEATURE_DETILS)
    fun getFeatureDetails(
        @Query("fpId") fpId:String?,
        @Query("clientId") clientId:String?
    ): Observable<Response<Array<GetFeatureDetailsItem>>>


}
package com.festive.poster.reset.services

import com.festive.poster.models.GetFeatureDetailsItem
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DevBoostRemoteData {

    @GET(EndPoints.GET_UPGRADE_DATA)
    fun getUpgradeData(
        @Header("Authorization") auth:String="591c0972ee786cbf48bd86cf",
        @Query("website") website:String="5e7a3cf46e0572000109a5b2",
    ): Observable<Response<UpgradeGetDataResponse>>


}
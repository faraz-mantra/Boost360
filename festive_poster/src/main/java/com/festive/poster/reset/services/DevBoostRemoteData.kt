package com.festive.poster.reset.services

import com.festive.poster.models.GetFeatureDetailsItem
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface DevBoostRemoteData {

    @GET
    fun getUpgradeData(
            @Header("Authorization") auth:String="591c0972ee786cbf48bd86cf",
            @Url url: String? = FirebaseRemoteConfigUtil.kAdminUrl(),
    ): Observable<Response<UpgradeGetDataResponse>>


}
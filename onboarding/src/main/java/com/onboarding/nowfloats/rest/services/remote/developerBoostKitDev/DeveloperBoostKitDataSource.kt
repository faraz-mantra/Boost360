package com.onboarding.nowfloats.rest.services.remote.developerBoostKitDev

import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DeveloperBoostKitDataSource {

    @GET(EndPoints.GET_FEATURE_SUPPORT_VIDEOS)
    fun getSupportVideos(
        @Header("Authorization") auth:String? = "597ee93f5d64370820a6127c",
        @Query("website") website: String? = "61278bf6f2e78f0001811865"): Observable<Response<FeatureSupportVideoResponse>>
}
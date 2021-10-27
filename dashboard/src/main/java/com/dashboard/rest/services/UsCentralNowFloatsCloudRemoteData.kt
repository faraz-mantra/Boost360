package com.dashboard.rest.services

import com.dashboard.model.DisableBadgeNotificationRequest
import com.dashboard.model.live.premiumBanner.UpgradePremiumFeatureResponse
import com.dashboard.rest.EndPoints
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsCentralNowFloatsCloudRemoteData {

    @POST(EndPoints.DISABLE_NOTIFICATION)
    fun disableBadgeNotification(
        @Body request : DisableBadgeNotificationRequest,
    ): Observable<Response<UpgradePremiumFeatureResponse>>
}
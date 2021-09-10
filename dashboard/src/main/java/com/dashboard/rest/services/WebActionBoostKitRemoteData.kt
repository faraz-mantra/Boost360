package com.dashboard.rest.services

import com.dashboard.model.OwnersDataResponse
import com.dashboard.model.RequestAddOwnersInfo
import com.dashboard.model.UpdateOwnersDataRequest
import com.dashboard.rest.EndPoints
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface WebActionBoostKitRemoteData {

    @POST(EndPoints.OWNER_INFO_ADD_DATA)
    fun addOwnersDataPost(@Header("Authorization") auth: String?, @Body body: RequestAddOwnersInfo?): Observable<Response<BaseResponse>>

    @POST(EndPoints.OWNER_INFO_UPDATE_DATA)
    fun updateOwnersDataPost(@Header("Authorization") auth: String?, @Body body: UpdateOwnersDataRequest?): Observable<Response<BaseResponse>>

    //String.format({"$and:[{ WebsiteId : 'TANZO'}]}", websiteId)
    @GET(EndPoints.OWNER_INFO_DATA)
    fun getOwnersDataPost(@Header("Authorization") token: String?, @Query("WebsiteId") websiteId: String?, @Query("limit") limit: Int?): Observable<Response<OwnersDataResponse>>
}
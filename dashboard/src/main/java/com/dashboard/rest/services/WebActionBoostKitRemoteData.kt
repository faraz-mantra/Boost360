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

    @Headers("X-Auth-Version: 2")
    @POST(EndPoints.OWNER_INFO_ADD_DATA)
    fun addOwnersDataPost(@Header("X-User-Id") auth: String?, @Body body: RequestAddOwnersInfo?): Observable<Response<BaseResponse>>

    @Headers("X-Auth-Version: 2")
    @POST(EndPoints.OWNER_INFO_UPDATE_DATA)
    fun updateOwnersDataPost(@Header("X-User-Id") auth: String?, @Body body: UpdateOwnersDataRequest?): Observable<Response<BaseResponse>>

    //String.format({"$and:[{ WebsiteId : 'TANZO'}]}", websiteId)
    @Headers("X-Auth-Version: 2")
    @GET(EndPoints.OWNER_INFO_DATA)
    fun getOwnersDataPost(@Header("X-User-Id") token: String?, @Query("query") query: String?, @Query("limit") limit: Int?): Observable<Response<OwnersDataResponse>>
}
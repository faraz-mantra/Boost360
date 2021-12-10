package com.dashboard.rest.services

import com.dashboard.rest.EndPoints
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface API2WithFloatsRemoteData {

    @GET(EndPoints.REPUBLISH_WEBSITE)
    fun republishWebsite(
        @Query("clientId") clientId:String,
        @Query("key") fpTag:String
    ): Observable<Response<Any>>
}
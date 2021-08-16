package com.dashboard.rest.services

import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.rest.EndPoints
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.io.File

interface WithFloatTwoRemoteData {
  @PUT(EndPoints.CREATE_BUSINESS_LOGO)
  fun uploadBusinessImage(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("reqType") reqType: String?,
    @Query("reqtId") reqtId: String?,
    @Query("totalChunks") totalChunks: String?,
    @Query("currentChunkNumber") currentChunkNumber: String?,
    @Body file: RequestBody?
  ): Observable<Response<ResponseBody>>


  @POST(EndPoints.FLOATING_POINT_UPDATE)
  fun updateBusinessProfile(
    @Body profileUpdateRequest: BusinessProfileUpdateRequest
  ): Observable<Response<ResponseBody>>

}
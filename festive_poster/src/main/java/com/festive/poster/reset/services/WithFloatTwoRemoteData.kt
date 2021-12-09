package com.festive.poster.reset.services

import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.reset.EndPoints
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import io.reactivex.Observable
import retrofit2.Response

interface WithFloatTwoRemoteData {


  @GET(EndPoints.GET_BIZ_WEB_UPDATE_BY_ID)
  fun getBizWebMessage(
    @Path("id") id: String?,
    @Query("clientId") clientId: String?
  ): Observable<Response<ResponseBody>>

  @PUT(EndPoints.PUT_BIZ_MESSAGE)
  fun putBizMessageUpdate(@Body request: PostUpdateTaskRequest?): Observable<Response<Any>>

  @Headers("Accept: application/json", "Content-Type: application/octet-stream")
  @PUT(EndPoints.PUT_BIZ_IMAGE)
  fun putBizImageUpdate(
    @Query("clientId") clientId: String?,
    @Query("requestType") requestType: String?,
    @Query("requestId") requestId: String?,
    @Query("totalChunks") totalChunks: Int?,
    @Query("currentChunkNumber") currentChunkNumber: Int?,
    @Query("socialParmeters") socialParmeters: String?,
    @Query("bizMessageId") bizMessageId: String?,
    @Query("sendToSubscribers") sendToSubscribers: Boolean?,
    @Body requestBody: RequestBody?,
  ): Observable<Response<String>>

//  fun getDeliveryDetails(): Observable<Response<ResponseBody>>


}
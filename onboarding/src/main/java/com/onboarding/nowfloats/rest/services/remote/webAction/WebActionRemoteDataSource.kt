package com.onboarding.nowfloats.rest.services.remote.webAction

import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.rest.EndPoints
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface WebActionRemoteDataSource {

  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.POST_UPDATE_WHATSAPP_URL)
  fun updateWhatsAppNumber(
    @Header("X-User-Id") auth: String,
    @Body request: UpdateChannelActionDataRequest
  ): Observable<Response<String>>

  @Headers("X-Auth-Version: 2")
  @GET(EndPoints.GET_WHATSAPP_BUSINESS)
  fun getWhatsAppBusiness(
    @Header("X-User-Id") auth: String,
    @Query("query") request: String?,
    @Query("limit") limit: Int? = 1
  ): Observable<Response<ChannelWhatsappResponse>>
}
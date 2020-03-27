package com.onboarding.nowfloats.rest.services.remote.channel

import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.channel.respose.UpdateChannelAccessTokenResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ChannelRemoteDataSource {

  @POST(EndPoints.POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL)
  fun updateChannelAccessToken(@Url url: String = EndPoints.NFX_BASE_URL,
                               @Body request: UpdateChannelAccessTokenRequest):
          Observable<Response<UpdateChannelAccessTokenResponse>>

  @POST(EndPoints.POST_UPDATE_WHATSAPP_URL)
  fun updateWhatsAppNumber(@Url url: String = EndPoints.WEB_ACTION_BASE_URL,
                           @Body request: UpdateChannelActionDataRequest):
          Observable<Response<UpdateChannelAccessTokenResponse>>

}
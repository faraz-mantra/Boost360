package com.onboarding.nowfloats.rest.services.remote.webAction

import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.channel.respose.UpdateChannelAccessTokenResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WebActionRemoteDataSource {

  @POST(EndPoints.POST_UPDATE_WHATSAPP_URL)
  fun updateWhatsAppNumber(@Body request: UpdateChannelActionDataRequest, @Header("Authorization") auth: String):
      Observable<Response<UpdateChannelAccessTokenResponse>>

}
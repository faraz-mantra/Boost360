package com.onboarding.nowfloats.rest.services.remote.channel

import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.respose.UpdateChannelAccessTokenResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChannelRemoteDataSource {

  @POST(EndPoints.POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL)
  fun updateChannelAccessToken(@Body request: UpdateChannelAccessTokenRequest):
          Observable<Response<UpdateChannelAccessTokenResponse>>

}
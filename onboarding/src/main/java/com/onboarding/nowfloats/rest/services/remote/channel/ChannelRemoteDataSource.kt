package com.onboarding.nowfloats.rest.services.remote.channel

import com.onboarding.nowfloats.rest.response.channel.ChannelListResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface ChannelRemoteDataSource {

  @GET("v2/5e68921d2f00000842d8ad84")
  fun getChannels(): Observable<Response<ChannelListResponse>>

}
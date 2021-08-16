package com.onboarding.nowfloats.rest.services.remote.channel

import com.onboarding.nowfloats.model.channel.insights.ChannelInsightsResponse
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.respose.UpdateChannelAccessTokenResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.nfxProcess.NFXProcessRequest
import com.onboarding.nowfloats.rest.EndPoints
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChannelRemoteDataSource {

  @POST(EndPoints.POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL)
  fun updateChannelAccessToken(@Body request: UpdateChannelAccessTokenRequest):
      Observable<Response<UpdateChannelAccessTokenResponse>>

  @GET(EndPoints.GET_CHANNELS_ACCESS_TOKEN)
  fun getChannelsAccessToken(@Query("nowfloats_id") nowfloatsId: String?): Observable<Response<ChannelsAccessTokenResponse>>

  @GET(EndPoints.NFX_CHANNELS_STATUS)
  fun getChannelsStatus(@Query("nowfloats_id") nowfloatsId: String?): Observable<Response<ChannelAccessStatusResponse>>

  @GET(EndPoints.NFX_CHANNELS_INSIGHTS)
  fun getChannelsInsights(
    @Query("nowfloats_id") nowfloatsId: String?,
    @Query("identifier") identifier: String?
  ): Observable<Response<ChannelInsightsResponse>>

  @POST(EndPoints.NFX_PROCESS_URL)
  fun nfxProcess(@Body request: NFXProcessRequest?): Observable<Response<Any>>

}
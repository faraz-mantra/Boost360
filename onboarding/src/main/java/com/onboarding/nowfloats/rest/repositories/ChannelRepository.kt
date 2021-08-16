package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken.AccessTokenType
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.nfxProcess.NFXProcessRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.NfxApiClient
import com.onboarding.nowfloats.rest.services.local.channel.ChannelLocalDataSource
import com.onboarding.nowfloats.rest.services.remote.channel.ChannelRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object ChannelRepository : AppBaseRepository<ChannelRemoteDataSource, ChannelLocalDataSource>() {

  override fun getRemoteDataSourceClass(): Class<ChannelRemoteDataSource> {
    return ChannelRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): ChannelLocalDataSource {
    return ChannelLocalDataSource
  }

  fun updateChannelAccessTokens(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return when (request.accessToken.getType()) {
      AccessTokenType.facebookpage -> postUpdateFacebookPageAccessToken(request)
      AccessTokenType.facebookshop -> postUpdateFacebookShopAccessToken(request)
      AccessTokenType.twitter -> postUpdateTwitterAccessToken(request)
      AccessTokenType.googlemap,
      AccessTokenType.googlesearch,
      AccessTokenType.googlemybusiness,
      -> postUpdateGoogleMyBusinessToken(request)
    }
  }

  fun getChannelsAccessToken(nowfloatsId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getChannelsAccessToken(nowfloatsId),
      Taskcode.GET_CHANNELS_ACCESS_TOKEN
    )
  }

  fun getChannelsStatus(nowfloatsId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getChannelsStatus(nowfloatsId),
      Taskcode.GET_CHANNELS_STATUS
    )
  }

  fun getChannelsInsights(nowfloatsId: String?, identifier: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getChannelsInsights(nowfloatsId, identifier),
      Taskcode.GET_CHANNELS_INSIGHTS
    )
  }

  fun nfxProcess(request: NFXProcessRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.nfxProcess(request), Taskcode.NFX_PROCESS_TASK)
  }

  fun postUpdateFacebookPageAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateChannelAccessToken(request = request),
      Taskcode.POST_UPDATE_FACEBOOK_PAGE_TOKEN
    )
  }

  fun postUpdateFacebookShopAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateChannelAccessToken(request = request),
      Taskcode.POST_UPDATE_FACEBOOK_SHOP_TOKEN
    )
  }

  fun postUpdateTwitterAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateChannelAccessToken(request = request),
      Taskcode.POST_UPDATE_TWITTER_TOKEN
    )
  }

  fun postUpdateGoogleMyBusinessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateChannelAccessToken(request = request),
      Taskcode.POST_UPDATE_GOOGLE_MY_BUSINESS_TOKEN
    )
  }

  override fun getApiClient(): Retrofit {
    return NfxApiClient.shared.retrofit
  }
}
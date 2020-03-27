package com.onboarding.nowfloats.rest.repositories

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken.*
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.services.local.channel.ChannelLocalDataSource
import com.onboarding.nowfloats.rest.services.remote.channel.ChannelRemoteDataSource
import io.reactivex.Observable

object ChannelRepository : AppBaseRepository<ChannelRemoteDataSource, ChannelLocalDataSource>() {

  override fun getRemoteDataSourceClass(): Class<ChannelRemoteDataSource> {
    return ChannelRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): ChannelLocalDataSource {
    return ChannelLocalDataSource
  }

  fun getChannels(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(localDataSource.getChannels(context), Taskcode.GET_CHANNELS)
  }

  fun updateChannelAccessTokens(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return when(request.accessToken.type){
      AccessTokenType.Facebookpage -> postUpdateFacebookPageAccessToken(request)
      AccessTokenType.Facebookshop -> postUpdateFacebookShopAccessToken(request)
      AccessTokenType.Twitter -> postUpdateTwitterAccessToken(request)
      AccessTokenType.GoogleMyBusiness -> postUpdateGoogleMyBusinessToken(request)
    }
  }

  fun postUpdateFacebookPageAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.updateChannelAccessToken(request = request), Taskcode.POST_UPDATE_FACEBOOK_PAGE_TOKEN)
  }
  fun postUpdateFacebookShopAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.updateChannelAccessToken(request = request), Taskcode.POST_UPDATE_FACEBOOK_SHOP_TOKEN)
  }
  fun postUpdateTwitterAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.updateChannelAccessToken(request = request), Taskcode.POST_UPDATE_TWITTER_TOKEN)
  }

  fun postUpdateGoogleMyBusinessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.updateChannelAccessToken(request = request), Taskcode.POST_UPDATE_GOOGLE_MY_BUSINESS_TOKEN)
  }
}
package com.onboarding.nowfloats.rest.repositories

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
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
//        return makeRemoteRequest(remoteDataSource.getChannels(), Taskcode.GET_CHANNELS)
    return makeLocalRequest(localDataSource.getChannels(context), Taskcode.GET_CHANNELS)
  }
}
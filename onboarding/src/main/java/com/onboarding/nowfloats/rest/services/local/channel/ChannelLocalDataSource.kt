package com.onboarding.nowfloats.rest.services.local.channel

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.rest.response.channel.ChannelListResponse
import io.reactivex.Observable

object ChannelLocalDataSource : AppBaseLocalService() {

  fun getChannels(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.channels, ChannelListResponse::class.java)
  }
}
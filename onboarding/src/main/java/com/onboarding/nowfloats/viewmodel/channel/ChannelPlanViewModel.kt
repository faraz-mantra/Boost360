package com.onboarding.nowfloats.viewmodel.channel

import android.content.Context
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.FeatureRepository
import io.reactivex.Observable

class ChannelPlanViewModel : BaseViewModel() {

  fun getChannels(context: Context): LiveData<BaseResponse> {
    return ChannelRepository.getChannels(context).toLiveData()
  }

  fun getFeatures(context: Context): LiveData<BaseResponse> {
    return FeatureRepository.getFeatures(context).toLiveData()
  }

  fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return ChannelRepository.updateChannelAccessTokens(request)
  }
}
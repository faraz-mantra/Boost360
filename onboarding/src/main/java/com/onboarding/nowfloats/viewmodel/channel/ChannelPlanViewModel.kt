package com.onboarding.nowfloats.viewmodel.channel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.rest.repositories.BoostWebRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import io.reactivex.Observable

class ChannelPlanViewModel : BaseViewModel() {

  fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): Observable<BaseResponse> {
    return ChannelRepository.updateChannelAccessTokens(request)
  }

  fun getMerchantProfile(floatingpointId: String?): LiveData<BaseResponse> {
    return BoostWebRepository.getMerchantProfile(floatingpointId).toLiveData()
  }
}
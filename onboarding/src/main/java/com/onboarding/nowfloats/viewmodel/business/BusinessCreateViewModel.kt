package com.onboarding.nowfloats.viewmodel.business

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.rest.repositories.BusinessCreateRepository
import com.onboarding.nowfloats.rest.repositories.BusinessDomainRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository

class BusinessCreateViewModel : BaseViewModel() {

    fun createBusinessOnboarding(request: BusinessCreateRequest): LiveData<BaseResponse> {
        return BusinessCreateRepository.postCreateBusinessOnboarding(request).toLiveData()
    }

    fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): LiveData<BaseResponse> {
        return ChannelRepository.updateChannelAccessTokens(request).toLiveData()
    }

    fun postUpdateWhatsappRequest(request: UpdateChannelActionDataRequest): LiveData<BaseResponse> {
        return WhatsAppRepository.postUpdateWhatsappRequest(request).toLiveData()
    }

    fun checkBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
        return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
    }
}
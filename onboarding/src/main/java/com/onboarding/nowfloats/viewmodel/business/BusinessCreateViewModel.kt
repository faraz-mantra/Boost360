package com.onboarding.nowfloats.viewmodel.business

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainSuggestRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileProfileRequest
import com.onboarding.nowfloats.rest.repositories.*
import org.json.JSONObject

class BusinessCreateViewModel : BaseViewModel() {

  fun putCreateBusinessOnboarding(profileId: String?, request: BusinessCreateRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessOnboarding(profileId, request).toLiveData()
  }

  fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): LiveData<BaseResponse> {
    return ChannelRepository.updateChannelAccessTokens(request).toLiveData()
  }

  fun postUpdateWhatsappRequest(request: UpdateChannelActionDataRequest, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.postUpdateWhatsappRequest(request, auth).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(getJsonRequest(request), auth).toLiveData()
  }

  fun postCheckBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
    return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
  }

  fun postCheckBusinessDomainSuggest(request: BusinessDomainSuggestRequest): LiveData<BaseResponse> {
    return BusinessDomainRepository.postCheckBusinessDomainSuggest(request).toLiveData()
  }

  fun putUploadImageBusiness(request: UploadFileBusinessRequest): LiveData<BaseResponse> {
    return UploadImageRepository.putUploadImageBusiness(request).toLiveData()
  }

  fun putUploadImageProfile(request: UploadFileProfileRequest): LiveData<BaseResponse> {
    return UploadImageRepository.putUploadImageProfile(request).toLiveData()
  }

}

fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}
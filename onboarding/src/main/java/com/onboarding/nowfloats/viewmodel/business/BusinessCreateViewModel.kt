package com.onboarding.nowfloats.viewmodel.business

import android.content.Context
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.business.purchasedOrder.ActivatePurchasedOrderRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainSuggestRequest
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthTokenRequest
import com.onboarding.nowfloats.model.riaWhatsapp.RiaWhatsappRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileProfileRequest
import com.onboarding.nowfloats.model.verification.RequestValidateEmail
import com.onboarding.nowfloats.model.verification.RequestValidatePhone
import com.onboarding.nowfloats.rest.repositories.*
import org.json.JSONObject

class BusinessCreateViewModel : BaseViewModel() {

  fun putCreateBusinessOnboarding(
    profileId: String?,
    request: BusinessCreateRequest
  ): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessOnboarding(profileId, request).toLiveData()
  }

  fun postActivatePurchasedOrder(
    clientId: String?,
    request: ActivatePurchasedOrderRequest
  ): LiveData<BaseResponse> {
    return BusinessCreateRepository.postActivatePurchasedOrder(clientId, request).toLiveData()
  }

  fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): LiveData<BaseResponse> {
    return ChannelRepository.updateChannelAccessTokens(request).toLiveData()
  }

  fun postUpdateWhatsappRequest(
    request: UpdateChannelActionDataRequest,
    auth: String
  ): LiveData<BaseResponse> {
    return WhatsAppRepository.postUpdateWhatsappRequest(auth = auth, request = request).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(auth = auth, request = getJsonRequest(request))
      .toLiveData()
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

  fun getAccountLocationsGMB(auth: String?, userId: String?): LiveData<BaseResponse> {
    return GMBRepository.getAccountLocations(auth, userId).toLiveData()
  }

  fun getGoogleAuthToken(req: GoogleAuthTokenRequest?): LiveData<BaseResponse> {
    return GoogleAuthRepository.getGoogleAuthToken(req).toLiveData()
  }

  fun updateRiaWhatsapp(req: RiaWhatsappRequest?): LiveData<BaseResponse> {
    return RiaFloatWhatsappRepository.updateRiaWhatsapp(req).toLiveData()
  }

  fun validateUsersEmail(requestValidateEmail: RequestValidateEmail?): LiveData<BaseResponse> {
    return BusinessCreateRepository.validateUsersEmail(requestValidateEmail = requestValidateEmail)
      .toLiveData()
  }

  fun validateUsersPhone(requestValidatePhone: RequestValidatePhone?): LiveData<BaseResponse> {
    return BusinessCreateRepository.validateUsersPhone(requestValidatePhone = requestValidatePhone)
      .toLiveData()
  }

  fun getCategoriesPlan(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategoriesPlan(context).toLiveData()
  }

}

fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}
package com.boost.presignin.viewmodel

import BusinessDomainRequest
import androidx.lifecycle.LiveData
import com.boost.presignin.model.RequestFloatsModel
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.rest.repository.BusinessCreateRepository
import com.boost.presignin.rest.repository.BusinessDomainRepository
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class LoginSignUpViewModel : BaseViewModel() {
  fun checkMobileIsRegistered(number: Long?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.isMobileIsRegistered(number, clientId = clientId).toLiveData()
  }
  fun postActivatePurchasedOrder(clientId: String?, request: ActivatePurchasedOrderRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.postActivatePurchasedOrder(clientId, request).toLiveData()
  }
  fun getFpDetailsByPhone(number: Long?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpDetailsByPhone(number, clientId).toLiveData()
  }

  fun putCreateBusinessOnboarding(profileId: String?, request: BusinessCreateRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessOnboarding(profileId, request).toLiveData()
  }

  fun postCheckBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
    return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
  }

  fun createMerchantProfile(request: RequestFloatsModel?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createUserProfile(request!!).toLiveData()
  }

  fun sendOtpIndia(number: Long?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.sendOtpIndia(number, clientId).toLiveData()
  }

  fun verifyOtp(number: String?, otp: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.verifyOtp(number, otp, clientId).toLiveData()
  }

  fun getFpListByMobile(number: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpListForRegisteredNumber(number, clientId = clientId).toLiveData()
  }

  fun getFpDetails(fpId: String, map: Map<String, String>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpDetails(fpId, map).toLiveData()
  }

  fun verifyUserProfile(request: UserProfileVerificationRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.verifyUserProfile(request).toLiveData()
  }
}
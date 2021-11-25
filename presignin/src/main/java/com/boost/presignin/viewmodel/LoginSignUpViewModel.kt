package com.boost.presignin.viewmodel

import BusinessDomainRequest
import android.content.Context
import androidx.lifecycle.LiveData
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.login.ForgotPassRequest
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.verification.RequestValidateEmail
import com.boost.presignin.model.verification.RequestValidatePhone
import com.boost.presignin.rest.repository.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import io.reactivex.Observable

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

  fun putCreateBusinessV5(profileId: String?, request: BusinessCreateRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessV5(profileId, request).toLiveData()
  }

  fun putCreateBusinessV6(profileId: String?, request: BusinessCreateRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessV6(profileId, request).toLiveData()
  }

  fun postCheckBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
    return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
  }

  fun createMerchantProfile(request: CreateProfileRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createUserProfile(request).toLiveData()
  }

  fun sendOtpIndia(number: Long?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.sendOtpIndia(number, clientId).toLiveData()
  }

  fun verifyOtp(number: String?, otp: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.verifyOtp(number, otp, clientId).toLiveData()
  }

  fun verifyLoginOtp(number: String?, otp: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.verifyLoginOtp(number, otp, clientId).toLiveData()
  }

  fun createAccessToken(request: AccessTokenRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createAccessToken(request).toLiveData()
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

  fun forgotPassword(request: ForgotPassRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.forgotPassword(request).toLiveData()
  }

  fun validateUsersEmail(requestValidateEmail: RequestValidateEmail?): LiveData<BaseResponse> {
    return WithFloatRepository.validateUsersEmail(requestValidateEmail = requestValidateEmail).toLiveData()
  }

  fun validateUsersPhone(requestValidatePhone: RequestValidatePhone?): LiveData<BaseResponse> {
    return WithFloatRepository.validateUsersPhone(requestValidatePhone = requestValidatePhone).toLiveData()
  }

  fun whatsappOptIn(optType:Int,number:String,customerId:String): LiveData<BaseResponse> {
    return RiaWithFloatRepository.whatsappOptIn(optType,number,customerId).toLiveData()
  }

    fun getCategoriesPlan(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategoriesPlan(context).toLiveData()
  }
}
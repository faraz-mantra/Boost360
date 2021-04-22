package com.boost.presignin.viewmodel

import androidx.lifecycle.LiveData
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class LoginSignUpViewModel : BaseViewModel() {
  fun checkMobileIsRegistered(number: Long?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.isMobileIsRegistered(number,clientId =clientId ).toLiveData()
  }
  fun getFpDetailsByPhone(number: Long?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpDetailsByPhone(number,clientId).toLiveData()
  }

  fun sendOtpIndia(number: Long?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.sendOtpIndia(number,clientId).toLiveData()
  }

  fun verifyOtp(number: String?, otp: String?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.verifyOtp(number, otp,clientId).toLiveData()
  }

  fun getFpListByMobile(number: String?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpListForRegisteredNumber(number,clientId =clientId).toLiveData()
  }

  fun getFpDetails(fpId: String, map: Map<String, String>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpDetails(fpId, map).toLiveData()
  }
}
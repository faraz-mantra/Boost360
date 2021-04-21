package com.boost.presignin.viewmodel

import androidx.lifecycle.LiveData
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class LoginSignUpViewModel : BaseViewModel() {
    fun checkMobileIsRegistered(number: Long?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.isMobileIsRegistered(number).toLiveData()
    }

    fun getFpDetailsByPhone(number: Long?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getFpDetailsByPhone(number).toLiveData()
    }
    fun sendOtpIndia(number: Long?):LiveData<BaseResponse>{
        return WithFloatTwoRepository.sendOtpIndia(number).toLiveData()
    }
    fun verifyOtp(number: String?,otp:String?):LiveData<BaseResponse>{
        return WithFloatTwoRepository.verifyOtp(number,otp).toLiveData()
    }
    fun getFpListByMobile(number: String?):LiveData<BaseResponse>{
        return WithFloatTwoRepository.getFpListForRegisteredNumber(number).toLiveData()
    }

}
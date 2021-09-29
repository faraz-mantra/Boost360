package com.dashboard.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.dashboard.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import okhttp3.RequestBody

class UserProfileViewModel : BaseViewModel() {

    fun uploadProfileImage( clientId: String?,loginId:String?,fileName:String, file: RequestBody?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.uploadUserProfileImage(clientId,loginId,fileName,file).toLiveData()
    }
    fun getUserProfileData(loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getUserProfileData(loginId).toLiveData()
    }

    fun updateUserName(userName:String?,loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateUserName(userName,loginId).toLiveData()
    }

    fun sendEmailOTP(email:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.sendOtpEmail(email).toLiveData()
    }

    fun updateEmail(
        email:String?,
        otp:String?,
        loginId:String?
    ): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateEmail(email,otp,loginId).toLiveData()
    }

    fun sendMobileOTP(mobile:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.sendOtpMobile(mobile).toLiveData()
    }

    fun updateMobile(
        mobile:String?,
        otp:String?,
        loginId:String?
    ): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateMobile(mobile,otp,loginId).toLiveData()
    }

    fun updateWhatsappNo(
        mobile:String?,
        optIn: Boolean?,
        loginId:String?
    ): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateWhatsapp(mobile,optIn,loginId).toLiveData()
    }
}
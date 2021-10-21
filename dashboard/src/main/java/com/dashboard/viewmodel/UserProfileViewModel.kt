package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.rest.repository.WithFloatTwoRepositoryD
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.RequestBody

class UserProfileViewModel : BaseViewModel() {

    fun uploadProfileImage( clientId: String?,loginId:String?,fileName:String, file: RequestBody?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.uploadUserProfileImage(clientId,loginId,fileName,file).toLiveData()
    }
    fun getUserProfileData(loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.getUserProfileData(loginId).toLiveData()
    }

    fun updateUserName(userName:String?,loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.updateUserName(userName,loginId).toLiveData()
    }

    fun sendEmailOTP(email:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.sendOtpEmail(email).toLiveData()
    }

    fun updateEmail(email:String?, otp:String?, loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.updateEmail(email,otp,loginId).toLiveData()
    }

    fun sendMobileOTP(mobile:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.sendOtpMobile(mobile).toLiveData()
    }

    fun updateMobile(mobile:String?, otp:String?, loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.updateMobile(mobile,otp,loginId).toLiveData()
    }

    fun updateWhatsappNo(mobile:String?, optIn: Boolean?, loginId:String?): LiveData<BaseResponse> {
        return WithFloatTwoRepositoryD.updateWhatsapp(mobile,optIn,loginId).toLiveData()
    }
}
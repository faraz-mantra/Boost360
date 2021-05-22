package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.model.paymentKyc.update.UpdatePaymentKycRequest
import com.appservice.rest.repository.RazorRepository
import com.appservice.rest.repository.WebActionBoostKitRepository
import com.appservice.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.MultipartBody

class WebBoostKitViewModel : BaseViewModel() {

  fun getKycData(auth: String?,query: String?): LiveData<BaseResponse> {
    return WebActionBoostKitRepository.getKycData(auth,query).toLiveData()
  }

  fun getKycListData(auth: String?,): LiveData<BaseResponse> {
    return WebActionBoostKitRepository.getKycListData(auth).toLiveData()
  }

  fun addKycData(auth: String?,request: PaymentKycRequest?): LiveData<BaseResponse> {
    return WebActionBoostKitRepository.addKycData(auth,request).toLiveData()
  }

  fun updateKycData(auth: String?,request: UpdatePaymentKycRequest): LiveData<BaseResponse> {
    return WebActionBoostKitRepository.updateKycData(auth,request).toLiveData()
  }

  fun putUploadFile(auth: String?,file: MultipartBody.Part?, assetFileName: String?): LiveData<BaseResponse> {
    return WebActionBoostKitRepository.putUploadImageProfile(auth,file, assetFileName).toLiveData()
  }

  fun userAccountDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatRepository.userAccountDetail(fpId, clientId).toLiveData()
  }

  fun ifscDetail(ifsc: String?): LiveData<BaseResponse> {
    return RazorRepository.ifscDetail(ifsc).toLiveData()
  }
}
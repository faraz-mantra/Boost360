package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.LogotronBuildMyLogoRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import io.reactivex.Observable
import okhttp3.RequestBody

class BusinessLogoViewModel:BaseViewModel() {

    fun putUploadImageBusiness(fpId: String?,fileName:String?,requestBody: RequestBody?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.putUploadImageBusiness(
            fpId = fpId,
            fileName = fileName,
            requestBody = requestBody
        ).toLiveData()
    }

    fun archieveAlert(fpId: String?, type: String?): Observable<BaseResponse> {
        return WithFloatTwoRepository.archieveAlert(fpId, type)
    }

    fun addLogotronClient(firstName: String?, lastName: String?, email: String?, phoneNumber: String?, ): LiveData<BaseResponse> {
        return LogotronBuildMyLogoRepository.addLogotronClient(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber).toLiveData()
    }

    fun downloadLogotronImage(): LiveData<BaseResponse> {
        return LogotronBuildMyLogoRepository.downloadLogotronImage().toLiveData()
    }
}
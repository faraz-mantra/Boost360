package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.pref.clientId
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

    fun archieveAlert(fpId: String?,type:String?): Observable<BaseResponse> {
        return WithFloatTwoRepository.archieveAlert(fpId,type)

    }
}
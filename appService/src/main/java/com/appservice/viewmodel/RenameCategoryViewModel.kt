package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.businessmodel.BusinessProfileUpdateRequest
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class RenameCategoryViewModel:BaseViewModel() {
    fun updateBusinessDetails(businessProfileUpdateUrl:String, request: BusinessProfileUpdateRequest): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateBusinessProfile(businessProfileUpdateUrl,request).toLiveData()
    }
}
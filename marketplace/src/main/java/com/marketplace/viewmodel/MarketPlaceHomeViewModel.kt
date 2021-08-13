package com.marketplace.viewmodel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.marketplace.rest.repository.DeveloperBoostKitRepository

class MarketPlaceHomeViewModel:BaseViewModel() {

    fun getAllFeatures(auth:String = "591c0972ee786cbf48bd86cf",website:String = "5e7a3cf46e0572000109a5b2"): LiveData<BaseResponse> {
        return DeveloperBoostKitRepository.getAllFeatures(auth,website).toLiveData()
    }
}
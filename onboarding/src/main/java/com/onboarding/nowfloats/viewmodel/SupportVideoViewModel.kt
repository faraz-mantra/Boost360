package com.onboarding.nowfloats.viewmodel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.rest.repositories.DeveloperBoostKitDevRepository

class SupportVideoViewModel : BaseViewModel() {

    fun getSupportVideos(): LiveData<BaseResponse> {
        return DeveloperBoostKitDevRepository.getSupportVideos().toLiveData()
    }
}
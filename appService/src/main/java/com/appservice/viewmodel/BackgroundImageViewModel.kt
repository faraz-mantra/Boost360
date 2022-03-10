package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData


class BackgroundImageViewModel : BaseViewModel() {
    fun getImages(fpId: String, clientId: String): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getImages(fpId, clientId).toLiveData()
    }

    fun createBGImage(body: okhttp3.RequestBody, fpId: String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.createBGImage(body,fpId).toLiveData()
    }

    fun deleteBGImage(map: HashMap<String, String?>,): LiveData<BaseResponse> {
        return WithFloatTwoRepository.deleteBGImage(
                map
            ).toLiveData()

    }
}
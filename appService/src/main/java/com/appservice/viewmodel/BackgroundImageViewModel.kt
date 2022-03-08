package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.squareup.okhttp.RequestBody
import okhttp3.MultipartBody


class BackgroundImageViewModel : BaseViewModel() {
    fun getImages(fpId: String, clientId: String): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getImages(fpId, clientId).toLiveData()
    }

    fun createBGImage(body: okhttp3.RequestBody, fpId: String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.createBGImage(body,fpId).toLiveData()
    }
}
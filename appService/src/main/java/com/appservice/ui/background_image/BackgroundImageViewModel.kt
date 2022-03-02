package com.appservice.ui.background_image

import androidx.lifecycle.LiveData
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData


class BackgroundImageViewModel : BaseViewModel() {
    fun getImages(fpId: String, clientId: String): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getImages(fpId, clientId).toLiveData()
    }
}
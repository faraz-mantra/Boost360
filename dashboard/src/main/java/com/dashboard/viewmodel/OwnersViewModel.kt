package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.model.RequestAddOwnersInfo
import com.dashboard.model.UpdateOwnersDataRequest
import com.dashboard.rest.repository.WebActionBoostKitRepository
import com.dashboard.rest.repository.WebActionKitsuneRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.MultipartBody

class OwnersViewModel : BaseViewModel() {
    fun getOwnersData(auth: String? ="5b34c22334afc405180cb70e", query: String?, limit: Int?=1): LiveData<BaseResponse> {
        return WebActionBoostKitRepository.getOwnersDataPost(auth, query, limit).toLiveData()
    }

    fun addOwnersData(auth: String?="5b34c22334afc405180cb70e", request: RequestAddOwnersInfo): LiveData<BaseResponse> {
        return WebActionBoostKitRepository.addOwnersDataPost(auth, request).toLiveData()
    }

    fun updateOwnersData(auth: String?="5b34c22334afc405180cb70e", request: UpdateOwnersDataRequest?): LiveData<BaseResponse> {
        return WebActionBoostKitRepository.updateOwnersDataPost(auth, request).toLiveData()
    }

    fun uploadImageProfile(auth: String?="5b34c22334afc405180cb70e", assetFileName: String?, file: MultipartBody.Part?): LiveData<BaseResponse> {
        return WebActionKitsuneRepository.uploadOwnersProfileImage(auth, assetFileName, file).toLiveData()
    }
}
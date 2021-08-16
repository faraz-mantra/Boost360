package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.RequestBody

class BusinessProfileViewModel : BaseViewModel() {
  fun updateBusinessProfile(request: BusinessProfileUpdateRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.updateBusinessProfile(request).toLiveData()
  }

  fun putUploadBusinessLogo(
    clientId: String?,
    fpId: String?,
    reqType: String?,
    reqId: String?,
    totalChunks: String?,
    currentChunkNumber: String?,
    file: RequestBody?
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepository.uploadBusinessLogo(
      clientId,
      fpId,
      reqType,
      reqId,
      totalChunks,
      currentChunkNumber,
      file
    ).toLiveData()
  }
}
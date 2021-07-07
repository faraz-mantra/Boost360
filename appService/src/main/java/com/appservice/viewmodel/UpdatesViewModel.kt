package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.RequestBody

class UpdatesViewModel : BaseViewModel() {

  fun getMessageUpdates(map: Map<String?, String?>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getMessageUpdates(map).toLiveData()
  }

  fun putBizMessageUpdate(request: PostUpdateTaskRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizMessageUpdate(request).toLiveData()
  }

  fun deleteBizMessageUpdate(request: DeleteBizMessageRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.deleteBizMessageUpdate(request).toLiveData()
  }

  fun getBizWebMessage(id: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getBizWebMessage(id, clientId).toLiveData()
  }

  fun putBizImageUpdate(
    clientId: String?,
    requestType: String?,
    requestId: String?,
    totalChunks: Int?,
    currentChunkNumber: Int?,
    socialParmeters: String?,
    bizMessageId: String?,
    sendToSubscribers: Boolean?,
    requestBody: RequestBody?,
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizImageUpdate(
      clientId, requestType, requestId, totalChunks,
      currentChunkNumber, socialParmeters, bizMessageId, sendToSubscribers, requestBody
    ).toLiveData()
  }
}
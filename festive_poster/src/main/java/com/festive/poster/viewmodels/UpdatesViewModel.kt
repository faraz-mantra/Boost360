package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.reset.repo.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.squareup.okhttp.RequestBody

class UpdatesViewModel : BaseViewModel() {


  fun putBizMessageUpdate(request: PostUpdateTaskRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizMessageUpdate(request).toLiveData()
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
    requestBody: okhttp3.RequestBody?,
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizImageUpdate(
      clientId, requestType, requestId, totalChunks,
      currentChunkNumber, socialParmeters, bizMessageId, sendToSubscribers, requestBody
    ).toLiveData()
  }
}
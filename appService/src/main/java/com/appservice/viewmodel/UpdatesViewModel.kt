package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.framework.models.UpdateDraftBody
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.UsCentralNowFloatsCloudRepo
import com.appservice.rest.repository.WithFloatRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import io.reactivex.Observable
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
  fun uploadDraftImage(id: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getBizWebMessage(id, clientId).toLiveData()
  }

  fun updateFirebaseState(id: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getBizWebMessage(id, clientId).toLiveData()
  }

  fun updateDraft(updateDraftBody: UpdateDraftBody): LiveData<BaseResponse> {
    return UsCentralNowFloatsCloudRepo.updateDraft(
      updateDraftBody
    ).toLiveData()
  }

  fun putUploadImage(request: UploadFileBusinessRequest): LiveData<BaseResponse> {
    return WithFloatRepository.putUploadImage(request).toLiveData()
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
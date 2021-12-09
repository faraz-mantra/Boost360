package com.festive.poster.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.reset.repo.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository
import com.onboarding.nowfloats.viewmodel.category.getJsonRequest
import com.squareup.okhttp.RequestBody

class PostUpdatesViewModel : BaseViewModel() {


  fun putBizMessageUpdate(request: PostUpdateTaskRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizMessageUpdate(request).toLiveData()
  }

  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getChannelsAccessTokenStatus(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsStatus(nowfloatsId).toLiveData()
  }
  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(auth = auth, request = getJsonRequest(request))
      .toLiveData()
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
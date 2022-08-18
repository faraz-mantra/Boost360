package com.festive.poster.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.models.response.TemplateSaveActionBody
import com.festive.poster.models.promoModele.TagListRequest
import com.festive.poster.reset.repo.NowFloatsRepository
import com.festive.poster.reset.repo.UsCentralNowFloatsCloudRepo
import com.festive.poster.reset.repo.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.UpdateDraftBody
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

  fun getFavTemplates(floatingPointId: String?,floatingPointTag: String?,featureKey:String): LiveData<BaseResponse> {
    return NowFloatsRepository.getFavTemplates(floatingPointId,floatingPointTag,featureKey).toLiveData()
  }

  fun putBizMessageUpdateV2(request: PostUpdateTaskRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizMessageUpdateV2(request).toLiveData()
  }

 /* fun favPoster(floatingPointId: String?,fpTag: String?,templateId: String?): LiveData<BaseResponse> {
    return NowFloatsRepository.makeTempFav(floatingPointId,fpTag,templateId).toLiveData()
  }*/

  fun putBizImageUpdateV2(
    type: String?,
    bizMessageId: String?,
    imageBase64: String?,
    sendToSubscribers: Boolean?,
    socialParmeters: String?
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepository.putBizImageUpdateV2(
      type,bizMessageId,imageBase64,sendToSubscribers,socialParmeters
    ).toLiveData()

  }


  fun updateDraft(updateDraftBody: UpdateDraftBody): LiveData<BaseResponse> {
    return UsCentralNowFloatsCloudRepo.updateDraft(
      updateDraftBody
    ).toLiveData()
  }

  fun getUserDetails(fpTag: String?, clientId: String): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getUserDetails(fpTag,clientId).toLiveData()
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

  fun getMerchantSummary(clientId: String?,fpTag: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getMerchantSummary(clientId,fpTag).toLiveData()
  }





  fun getTemplateConfig(fKey:String,floatingPointId: String?,floatingPointTag: String?): LiveData<BaseResponse> {
    return NowFloatsRepository.getTemplateConfig(fKey,floatingPointId,floatingPointTag).toLiveData()
  }


}
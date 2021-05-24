package com.onboarding.nowfloats.viewmodel.category

import android.content.Context
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.nfxProcess.NFXProcessRequest
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository
import org.json.JSONObject

class CategoryViewModel : BaseViewModel() {

  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getCategoriesPlan(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategoriesPlan(context).toLiveData()
  }

  @Deprecated("NFX token API")
  fun getChannelsAccessToken(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsAccessToken(nowfloatsId).toLiveData()
  }

  fun getChannelsAccessTokenStatus(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsStatus(nowfloatsId).toLiveData()
  }

  fun updateChannelAccessToken(request: UpdateChannelAccessTokenRequest): LiveData<BaseResponse> {
    return ChannelRepository.updateChannelAccessTokens(request).toLiveData()
  }

  fun postUpdateWhatsappRequest(request: UpdateChannelActionDataRequest, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.postUpdateWhatsappRequest(auth = auth, request = request).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(auth = auth, request = getJsonRequest(request)).toLiveData()
  }

  fun nfxProcess(request: NFXProcessRequest?): LiveData<BaseResponse> {
    return ChannelRepository.nfxProcess(request).toLiveData()
  }
}

fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}
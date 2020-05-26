package com.onboarding.nowfloats.viewmodel.category

import android.content.Context
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository
import org.json.JSONObject

class CategoryViewModel : BaseViewModel() {

  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getChannelsAccessToken(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsAccessToken(nowfloatsId).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(getJsonRequest(request), auth).toLiveData()
  }

}

fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}
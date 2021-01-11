package com.dashboard.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.appservice.rest.repository.KitWebActionRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.dashboard.rest.repository.DevBoostKitRepository
import com.dashboard.rest.repository.PluginFloatRepository
import com.dashboard.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.inventoryorder.rest.repositories.ApiTwoWithFloatRepository
import com.inventoryorder.rest.repositories.InventoryOrderRepository
import com.onboarding.nowfloats.model.nfxProcess.NFXProcessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.UploadImageRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository
import org.json.JSONObject

class DashboardViewModel : BaseViewModel() {

  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getChannelsAccessToken(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsAccessToken(nowfloatsId).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(getJsonRequest(request), auth).toLiveData()
  }

  fun nfxProcess(request: NFXProcessRequest?): LiveData<BaseResponse> {
    return ChannelRepository.nfxProcess(request).toLiveData()
  }

  fun getBizFloatMessage(request: Map<String, String>): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getBizFloatMessage(request).toLiveData()
  }

  fun fpOnboardingUpdate(auth: String?, request: OnBoardingUpdateModel?): LiveData<BaseResponse> {
    return KitWebActionRepository.fpOnboardingUpdate(auth, request).toLiveData()
  }

  fun getNotificationCount(clientId: String?, fpId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getNotificationCount(clientId, fpId).toLiveData()
  }

  fun getNavDashboardData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getNavDashboardData(context).toLiveData()
  }

  fun getQuickActionData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getQuickActionData(context).toLiveData()
  }

  fun getBoostAddOnsTop(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostAddOnsTop(context).toLiveData()
  }

  fun getWelcomeDashboardData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getWelcomeDashboardData(context).toLiveData()
  }

  fun getBoostUserDetailMessage(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostUserDetailMessage(context).toLiveData()
  }

  fun getBoostCustomerItem(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostCustomerItem(context).toLiveData()
  }

  fun getSellerSummary(clientId: String?, sellerId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerSummary(clientId, sellerId).toLiveData()
  }

  fun getUserSummary(clientId: String?, fpIdParent: String?, scope: String?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getUserSummary(clientId, fpIdParent, scope).toLiveData()
  }

  fun getMapVisits(fpTag: String?, mapData: Map<String, String>?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getMapVisits(fpTag, mapData).toLiveData()
  }

  fun getUserCallSummary(clientId: String?, fpIdParent: String?, identifierType: String?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getUserCallSummary(clientId, fpIdParent, identifierType).toLiveData()
  }

  fun putUploadSecondaryImage(request: UploadFileBusinessRequest): LiveData<BaseResponse> {
    return UploadImageRepository.putUploadSecondaryImage(request).toLiveData()
  }

  fun getUpgradePremiumBanner(website_id: String? = "5e7a3cf46e0572000109a5b2"): LiveData<BaseResponse> {
    return DevBoostKitRepository.getUpgradePremiumBanner(website_id = website_id).toLiveData()
  }

  fun getUpgradeDashboardBanner(website_id: String? = "5fd88e1fb456eb000133ad31"): LiveData<BaseResponse> {
    return DevBoostKitRepository.getUpgradeDashboardBanner(website_id = website_id).toLiveData()
  }

  fun getDomainDetailsForFloatingPoint(fpTag: String?, map: Map<String, String>?): LiveData<BaseResponse> {
    return PluginFloatRepository.getDomainDetailsForFloatingPoint(fpTag, map).toLiveData()
  }
}


fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}
package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.WebActionsApiClient
import com.onboarding.nowfloats.rest.services.remote.webAction.WebActionRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object WhatsAppRepository : AppBaseRepository<WebActionRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<WebActionRemoteDataSource> {
    return WebActionRemoteDataSource::class.java
  }

  fun postUpdateWhatsappRequest(request: UpdateChannelActionDataRequest, auth: String): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateWhatsAppNumber(auth = auth, request = request), Taskcode.POST_CREATE_BUSINESS_WHATSAPP)
  }

  fun getWhatsappBusiness(request: String?, auth: String): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getWhatsAppBusiness(auth = auth, request = request), Taskcode.GET_BUSINESS_WHATSAPP)
  }

  override fun getApiClient(): Retrofit {
    return WebActionsApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}
package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.riaWhatsapp.RiaWhatsappRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.RiaWithFloatsApiClient
import com.onboarding.nowfloats.rest.services.remote.riaWhatsapp.RiaWhatsappRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object RiaFloatWhatsappRepository :
  AppBaseRepository<RiaWhatsappRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<RiaWhatsappRemoteDataSource> {
    return RiaWhatsappRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun updateRiaWhatsapp(req: RiaWhatsappRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateRiaWhatsapp(req?.client_id, req),
      Taskcode.POST_GOOGLE_AUTH_TOKEN
    )
  }

  override fun getApiClient(): Retrofit {
    return RiaWithFloatsApiClient.shared.retrofit
  }
}
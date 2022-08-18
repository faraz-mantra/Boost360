package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.framework.models.UpdateDraftBody
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.UsCentralNowFloatsCloudApiClient
import com.appservice.rest.services.UsCentralNowFloatsRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit


object UsCentralNowFloatsCloudRepo : AppBaseRepository<UsCentralNowFloatsRemoteData, AppBaseLocalService>() {


  fun updateDraft(updateDraftBody: UpdateDraftBody): Observable<BaseResponse> {
    return UsCentralNowFloatsCloudRepo.makeRemoteRequest(
      remoteDataSource.updateDraft(updateDraftBody),
      TaskCode.UPDATE_DRAFT
    )
  }

  override fun getRemoteDataSourceClass(): Class<UsCentralNowFloatsRemoteData> {
    return UsCentralNowFloatsRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return UsCentralNowFloatsCloudApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}

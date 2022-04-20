package com.festive.poster.reset.repo


import com.framework.models.UpdateDraftBody
import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.UsCentralNowFloatsCloudApiClient
import com.festive.poster.reset.services.UsCentralNowFloatsRemoteData
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

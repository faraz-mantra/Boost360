package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.GMBApiClient
import com.onboarding.nowfloats.rest.services.remote.google.GMBRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object GMBRepository : AppBaseRepository<GMBRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<GMBRemoteDataSource> {
    return GMBRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getAccountList(auth: String?, userId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getAccountList(auth, userId), Taskcode.GET_GMB_ACCOUNT_LIST)
  }

  override fun getApiClient(): Retrofit {
    return GMBApiClient.shared.retrofit
  }
}
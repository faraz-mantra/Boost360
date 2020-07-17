package com.catlogservice.rest.repository

import com.catlogservice.base.rest.AppBaseLocalService
import com.catlogservice.base.rest.AppBaseRepository
import com.catlogservice.rest.TaskCode
import com.catlogservice.rest.apiClients.WithFloatsApiClient
import com.catlogservice.rest.services.ServiceCreateRemote
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object ServiceCreateRepository : AppBaseRepository<ServiceCreateRemote, AppBaseLocalService>() {

  fun createService(request: Any): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createService(request), TaskCode.POST_CREATE_SERVICE)
  }

  override fun getRemoteDataSourceClass(): Class<ServiceCreateRemote> {
    return ServiceCreateRemote::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}

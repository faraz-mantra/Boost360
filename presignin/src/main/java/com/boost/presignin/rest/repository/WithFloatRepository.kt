package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.rest.services.WithFloatRemoteData
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
import retrofit2.Retrofit

object WithFloatRepository : AppBaseRepository<WithFloatRemoteData, AppBaseLocalService>() {

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatRemoteData> {
    return WithFloatRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

}

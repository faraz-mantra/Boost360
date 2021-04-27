package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
import com.boost.presignin.rest.services.remote.WithFloatsRemoteDataSource
import retrofit2.Retrofit

object WithFloatRepository : AppBaseRepository<WithFloatsRemoteDataSource, AppBaseLocalService>() {

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatsRemoteDataSource> {
    return WithFloatsRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

}

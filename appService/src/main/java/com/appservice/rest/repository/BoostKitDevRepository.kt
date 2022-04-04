package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.apiClients.RazorApiClient
import com.appservice.rest.services.BoostKitDevRemoteData
import retrofit2.Retrofit

object BoostKitDevRepository : AppBaseRepository<BoostKitDevRemoteData, AppBaseLocalService>() {


  override fun getRemoteDataSourceClass(): Class<BoostKitDevRemoteData> {
    return BoostKitDevRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return RazorApiClient.shared.retrofit
  }
}

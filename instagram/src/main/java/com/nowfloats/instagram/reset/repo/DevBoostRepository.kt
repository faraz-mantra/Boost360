package com.nowfloats.instagram.reset.repo

import com.nowfloats.instagram.reset.apiClients.DevBoostKitApiClient
import com.nowfloats.instagram.reset.services.DevBoostRemoteData
import com.nowfloats.instagram.base.rest.AppBaseLocalService
import com.nowfloats.instagram.base.rest.AppBaseRepository
import retrofit2.Retrofit


object DevBoostRepository : AppBaseRepository<DevBoostRemoteData, AppBaseLocalService>() {



  override fun getRemoteDataSourceClass(): Class<DevBoostRemoteData> {
    return DevBoostRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return DevBoostKitApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}

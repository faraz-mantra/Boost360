package com.nowfloats.instagram.reset.repo


import com.nowfloats.instagram.reset.apiClients.NowFloatsApiClient
import com.nowfloats.instagram.reset.services.NowFloatsRemoteData
import com.nowfloats.instagram.base.rest.AppBaseLocalService
import com.nowfloats.instagram.base.rest.AppBaseRepository
import retrofit2.Retrofit


object NowFloatsRepository : AppBaseRepository<NowFloatsRemoteData, AppBaseLocalService>() {


  override fun getRemoteDataSourceClass(): Class<NowFloatsRemoteData> {
    return NowFloatsRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return NowFloatsApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}

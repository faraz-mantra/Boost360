package com.nowfloats.instagram.reset.repo



import com.nowfloats.instagram.reset.TaskCode
import com.nowfloats.instagram.reset.apiClients.WithFloatsTwoApiClient
import com.nowfloats.instagram.reset.services.WithFloatsRemoteData
import com.framework.base.BaseResponse
import com.nowfloats.instagram.base.rest.AppBaseLocalService
import com.nowfloats.instagram.base.rest.AppBaseRepository
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Retrofit


object WithFloatsRepository : AppBaseRepository<WithFloatsRemoteData, AppBaseLocalService>() {



  override fun getRemoteDataSourceClass(): Class<WithFloatsRemoteData> {
    return WithFloatsRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return WithFloatsTwoApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}

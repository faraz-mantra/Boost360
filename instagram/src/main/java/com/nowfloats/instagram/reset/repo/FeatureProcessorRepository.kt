package com.nowfloats.instagram.reset.repo



import com.nowfloats.instagram.reset.TaskCode
import com.nowfloats.instagram.reset.apiClients.FeatureProcessorApiClient
import com.nowfloats.instagram.reset.services.FeatureProcessorRemoteData
import com.framework.base.BaseResponse
import com.nowfloats.instagram.base.rest.AppBaseLocalService
import com.nowfloats.instagram.base.rest.AppBaseRepository
import io.reactivex.Observable
import retrofit2.Retrofit


object FeatureProcessorRepository : AppBaseRepository<FeatureProcessorRemoteData, AppBaseLocalService>() {




  override fun getRemoteDataSourceClass(): Class<FeatureProcessorRemoteData> {
    return FeatureProcessorRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return FeatureProcessorApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}

package com.festive.poster.reset.repo



import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.FeatureProcessorApiClient
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.reset.services.FeatureProcessorRemoteData
import com.festive.poster.reset.services.NowFloatsRemoteData
import com.framework.base.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.Query


object FeatureProcessorRepository : AppBaseRepository<FeatureProcessorRemoteData, AppBaseLocalService>() {


  fun getFeatureDetails(fpId: String?,fpTag: String?): Observable<BaseResponse> {

    return makeRemoteRequest(
      remoteDataSource.getFeatureDetails(fpId,fpTag),
      TaskCode.GET_TEMPLATE_CONFIG
    )
  }

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

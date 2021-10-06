package com.festive.poster.reset.repo



import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.reset.services.NowFloatsRemoteData
import com.framework.base.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.Query


object NowFloatsRepository : AppBaseRepository<NowFloatsRemoteData, AppBaseLocalService>() {

  fun getTemplates(floatingPointId: String?,floatingPointTag: String?,tags:List<String>?): Observable<BaseResponse> {

    val body = JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
      addProperty("tags", Gson().toJson(tags))
      addProperty("showFavourites",false)
    }
    return NowFloatsRepository.makeRemoteRequest(
      remoteDataSource.getTemplates(body),
      TaskCode.GET_TEMPLATES
    )
  }

  fun getTemplateConfig(floatingPointId: String?,floatingPointTag: String?): Observable<BaseResponse> {
    val body =JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
    }
    return NowFloatsRepository.makeRemoteRequest(
      remoteDataSource.getTemplateViewConfig(body),
      TaskCode.GET_TEMPLATE_CONFIG
    )
  }

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

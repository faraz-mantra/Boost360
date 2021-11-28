package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WebActionBoostKitClient
import com.boost.presignin.rest.services.remote.RiaWithFloatDataSource
import com.boost.presignin.rest.services.remote.WebActionBoostKitDataSource
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Retrofit

object RiaWithFloatRepository : AppBaseRepository<RiaWithFloatDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<RiaWithFloatDataSource> {
    return RiaWithFloatDataSource::class.java
  }

  fun whatsappOptIn(optType:Int,number:String,customerId:String): Observable<BaseResponse> {
    /*val body = JsonObject().apply {
      addProperty("optType",optType)
      addProperty("whatsappNumber",number)
      addProperty("customerId",customerId)
      addProperty("optinId","919381915059")

    }*/
    return makeRemoteRequest(remoteDataSource.whatsappOptIn(optType = optType, number = number, customerId = customerId), TaskCode.WHATSAPP_OPT_IN)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WebActionBoostKitClient.shared.retrofit
  }
}
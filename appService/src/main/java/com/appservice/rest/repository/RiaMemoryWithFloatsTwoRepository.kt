package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.VmnCallModel
import com.appservice.model.aptsetting.*
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.RiaMemoryWithFloatsApiClient
import com.appservice.rest.apiClients.WithFloatsApiTwoClient
import com.appservice.rest.services.RiaMemoryWithFloatsRemoteData
import com.appservice.rest.services.WithFloatTwoRemoteData
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.QueryMap

object RiaMemoryWithFloatsTwoRepository : AppBaseRepository<RiaMemoryWithFloatsRemoteData, AppBaseLocalService>() {


/*  fun getLastCallInfo(
    @QueryMap data: Map<*, *>?):Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.getLastCallInfo(data), TaskCode.GET_MERCHANT_SUMMARY)
  }*/

 /* fun getVmnSummary(
    @Query("clientId") clientId: String?,
    @Query("fpid") fpId: String?,
    @Query("identifierType") type: String?,
  ):Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.getVmnSummary(clientId,fpId,type), TaskCode.GET_MERCHANT_SUMMARY)

  }
*/
  fun getCallCountByType(
    @Query("fptag") fptag: String?,
    @Query("eventType") eventType: String?,
    @Query("eventChannel") eventChannel: String?,
  ):Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.getCallCountByType(fptag, eventType, eventChannel), TaskCode.GET_MERCHANT_SUMMARY)

  }

 /* fun requestVmn(
    @Body bodyMap: Map<String?, String?>?,
    @Query("authClientId") clientId: String?,
    @Query("fpTag") fpTag: String?,
  ):Observable<BaseResponse>{
    return makeRemoteRequest(remoteDataSource.requestVmn(bodyMap, clientId, fpTag), TaskCode.GET_MERCHANT_SUMMARY)

  }
*/

  override fun getRemoteDataSourceClass(): Class<RiaMemoryWithFloatsRemoteData> {
    return RiaMemoryWithFloatsRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return RiaMemoryWithFloatsApiClient.shared.retrofit
  }

}

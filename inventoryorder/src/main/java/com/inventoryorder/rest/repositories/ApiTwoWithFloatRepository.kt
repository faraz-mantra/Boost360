package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.Api2WithFloatClient
import com.inventoryorder.rest.services.WithFloatTwoDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object ApiTwoWithFloatRepository : AppBaseRepository<WithFloatTwoDataSource, AppBaseLocalService>() {

  fun getAllServiceList(clientId: String?, skipBy: Int?, fpTag: String?, identifierType: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getAllServiceList(clientId, skipBy, fpTag, identifierType), TaskCode.GET_ALL_SERVICES)
  }

  fun sendSMS(mobile: String?, message: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.sendSMS(mobile, message, clientId), TaskCode.SEND_SMS)
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatTwoDataSource> {
    return WithFloatTwoDataSource::class.java
  }

  fun getBizFloatMessage(map: Map<String, String>): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getBizFloatMessage(map), TaskCode.GET_BIZ_FLOAT_MESSAGE)
  }

  fun getProductList(fpTag : String?, clientId : String?, skipBy: Int?) : Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getAllProductList(fpTag, clientId, skipBy), TaskCode.GET_ALL_PRODUCT_LIST)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return Api2WithFloatClient.shared.retrofit
  }

}
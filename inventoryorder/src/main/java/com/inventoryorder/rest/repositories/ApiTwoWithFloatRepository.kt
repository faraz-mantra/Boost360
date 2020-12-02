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

  fun getUserSummary(clientId: String?, fpIdParent: String?, scope: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getUserSummary(clientId, fpIdParent, scope), TaskCode.GET_USER_SUMMARY)
  }

  fun getUserCallSummary(clientId: String?, fpIdParent: String?, identifierType: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getUserCallSummary(clientId, fpIdParent, identifierType), TaskCode.GET_USER_CALL_SUMMARY)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return Api2WithFloatClient.shared.retrofit
  }

}
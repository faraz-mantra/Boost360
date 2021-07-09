package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.ApiWithFloatClient
import com.inventoryorder.rest.services.WithFloatDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object ApiWithFloatRepository : AppBaseRepository<WithFloatDataSource, AppBaseLocalService>() {


  override fun getRemoteDataSourceClass(): Class<WithFloatDataSource> {
    return WithFloatDataSource::class.java
  }

  fun getUserSummary(fpTag: String?,clientId: String?, fpIdParent: String?, scope: String?, startDate: String?, endDate: String?): Observable<BaseResponse> {
    return ApiWithFloatRepository.makeRemoteRequest(remoteDataSource.getUserSummary(fpTag,clientId, fpIdParent, scope, startDate, endDate), TaskCode.GET_USER_SUMMARY)
  }

  fun getUserMessageCount(fpId: String?, clientId: String?, startDate: String?, endDate: String?): Observable<BaseResponse> {
    return ApiWithFloatRepository.makeRemoteRequest(remoteDataSource.getUserMessageCount(fpId, clientId, startDate, endDate), TaskCode.GET_USER_MESSAGE_COUNT)
  }

  fun getSubscriberCount(fpTag: String?, clientId: String?, startDate: String?, endDate: String?): Observable<BaseResponse> {
    return ApiWithFloatRepository.makeRemoteRequest(remoteDataSource.getSubscriberCount(fpTag, clientId, startDate, endDate), TaskCode.GET_SUBSCRIBER_COUNT)
  }

  fun getUserCallSummary(clientId: String?, fpIdParent: String?, identifierType: String?, startDate: String?, endDate: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getUserCallSummary(clientId, fpIdParent, identifierType, startDate, endDate), TaskCode.GET_USER_CALL_SUMMARY)
  }

  fun getMapVisits(fpTag: String?, mapData: Map<String, String>?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getMapVisits(fpTag, mapData), TaskCode.GET_MAP_SUMMARY)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return ApiWithFloatClient.shared.retrofit
  }

}
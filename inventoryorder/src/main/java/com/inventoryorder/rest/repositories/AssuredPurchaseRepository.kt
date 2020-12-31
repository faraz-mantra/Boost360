package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.AssuredPurchaseClient
import com.inventoryorder.rest.services.AssuredPurchaseDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object AssuredPurchaseRepository: AppBaseRepository<AssuredPurchaseDataSource, AppBaseLocalService>(){

    fun postOrderInitiate(clientId: String?, request: OrderInitiateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.initiateOrder(clientId, request), TaskCode.POST_ORDER_INITIATE)
    }

    fun postOrderUpdate(clientId: String?, request: OrderInitiateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.updateOrder(clientId, request), TaskCode.POST_ORDER_UPDATE)
    }

    fun updateExtraPropertyOrder(clientId: String?, request: UpdateExtraPropertyRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.updateExtraPropertyOrder(clientId, request), TaskCode.POST_ORDER_EXTRA_FILED_UPDATE)
    }

    override fun getRemoteDataSourceClass(): Class<AssuredPurchaseDataSource> {
        return AssuredPurchaseDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return AssuredPurchaseClient.shared.retrofit
    }
}
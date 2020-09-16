package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.services.AssuredPurchaseDataSource
import io.reactivex.Observable

object AssuredPurchaseRepository: AppBaseRepository<AssuredPurchaseDataSource, AppBaseLocalService>(){

    fun postOrderInitiate(clientId: String?, request: OrderInitiateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.initiateOrder(clientId, request), TaskCode.POST_ORDER_INITIATE)
    }

    override fun getRemoteDataSourceClass(): Class<AssuredPurchaseDataSource> {
        return AssuredPurchaseDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

}
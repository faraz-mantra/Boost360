package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.serviceProduct.Product
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.WithFloatsApiTwoClient
import com.appservice.rest.services.WithFloatTwoRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Retrofit

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, AppBaseLocalService>() {

  fun createService(request: Product?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createService(request), TaskCode.POST_CREATE_SERVICE)
  }

  fun updateService(request: ProductUpdate?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateService(request), TaskCode.POST_UPDATE_SERVICE)
  }

  fun deleteService(request: DeleteProductRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteService(request), TaskCode.POST_UPDATE_SERVICE)
  }

  fun addUpdateImageProductService(
      clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?,
      productId: String?, requestBody: RequestBody?,
  ): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addUpdateImageProductService(clientId, requestType, requestId, totalChunks,
        currentChunkNumber, productId, requestBody), TaskCode.ADD_UPDATE_IMAGE_PRODUCT_SERVICE)
  }

  fun getNotificationCount(clientId: String?, fpId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getNotificationCount(clientId, fpId), TaskCode.GET_NOTIFICATION)
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatTwoRemoteData> {
    return WithFloatTwoRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiTwoClient.shared.retrofit
  }
}

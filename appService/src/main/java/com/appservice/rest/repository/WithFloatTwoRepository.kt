package com.appservice.rest.repository

import com.appservice.appointment.model.*
import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.serviceProduct.CatalogProduct
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

  fun createService(request: CatalogProduct?): Observable<BaseResponse> {
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

  fun createProduct(request: CatalogProduct?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createProduct(request = request), TaskCode.POST_CREATE_PRODUCT)
  }

  fun updateProduct(request: ProductUpdate?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateProduct(request), TaskCode.POST_UPDATE_PRODUCT)
  }

  fun deleteProduct(request: DeleteProductRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteProduct(request), TaskCode.POST_UPDATE_PRODUCT)
  }

  fun addUpdateImageProduct(
          clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?,
          productId: String?, requestBody: RequestBody?,
  ): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addUpdateImageProduct(clientId, requestType, requestId, totalChunks,
            currentChunkNumber, productId, requestBody), TaskCode.ADD_UPDATE_IMAGE_PRODUCT_SERVICE)
  }

  fun getDeliveryDetails(floatingPointId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deliverySetupGet(floatingPointId, clientId), TaskCode.GET_DELIVERY_DETAILS)
  }

  fun invoiceSetup(request: InvoiceSetupRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.invoiceSetupPost(request), TaskCode.SETUP_INVOICE)
  }

  fun getPaymentProfileDetails(floatingPointId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.paymentProfileDetailsGet(floatingPointId, clientId), TaskCode.GET_PAYMENT_PROFILE_DETAILS)
  }

  fun setupDelivery(request: DeliverySetup): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deliverySetupPost(request), TaskCode.SETUP_DELIVERY)
  }

  fun addMerchantUPI(request: UpdateUPIRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.upiIdUpdate(request), TaskCode.ADD_MERCHANT_UPI)
  }

  fun addMerchantSignature(request: UploadMerchantSignature): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.uploadMerchantSignature(request), TaskCode.PUT_MERCHANT_SIGNATURE)
  }

  fun addBankAccount(fpId: String?, clientId: String?, request: AddBankAccountRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addBankAccounts(fpId = fpId, clientId, request), TaskCode.ADD_BANK_ACCOUNT)
  }

}

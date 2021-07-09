package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.SendMailRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.BoostFloatsApiClient
import com.inventoryorder.rest.services.InventoryOrderRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object ProductOrderRepository : AppBaseRepository<InventoryOrderRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<InventoryOrderRemoteDataSource> {
    return InventoryOrderRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getProductDetails(productId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getProductDetails(productId), TaskCode.PRODUCT_ORDER_DETAILS_TASK)
  }

  fun getDoctorData(fpTag: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getDoctorsData(fpTag), TaskCode.GET_DOCTOR_DATA)
  }

  fun sendMail(request: SendMailRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.sendMail(getHeaderEmail(),request), TaskCode.SEND_MAIL)
  }

  override fun getApiClient(): Retrofit {
    return BoostFloatsApiClient.shared.retrofit
  }

  private fun getHeaderEmail(): HashMap<String, String> {
    val header = HashMap<String, String>()
    header["Accept"] = "application/json"
    header["Content-Type"] = "application/json; charset=utf-8"
    header["Cache-Control"] = "max-age=640000"
    return header
  }
}
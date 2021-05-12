package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.model.paymentKyc.update.UpdatePaymentKycRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.WebActionBoostKitApiClient
import com.appservice.rest.services.WebActionBoostKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit

object WebActionBoostKitRepository : AppBaseRepository<WebActionBoostKitRemoteData, AppBaseLocalService>() {

  fun getKycData(query: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getKycData(query), TaskCode.GET_KYC_DATA)
  }

  fun getKycListData(): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getKycListData(), TaskCode.GET_KYC_LIST_DATA)
  }

  fun addKycData(request: PaymentKycRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addKycData(request), TaskCode.ADD_KYC_DATA)
  }

  fun updateKycData(request: UpdatePaymentKycRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateKycData(request), TaskCode.UPDATE_KYC_DATA)
  }

  fun putUploadImageProfile(file: MultipartBody.Part?, assetFileName: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.putUploadImageProfile(file, assetFileName), TaskCode.POST_FILE_UPLOAD)
  }

  override fun getRemoteDataSourceClass(): Class<WebActionBoostKitRemoteData> {
    return WebActionBoostKitRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WebActionBoostKitApiClient.shared.retrofit
  }
}

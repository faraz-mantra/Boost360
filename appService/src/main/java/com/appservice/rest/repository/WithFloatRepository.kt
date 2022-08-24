package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.account.AccountCreateRequest
import com.appservice.model.account.BankAccountDetailsN
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.WithFloatsApiClient
import com.appservice.rest.services.WithFloatRemoteData
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.repositories.UploadImageRepository
import io.reactivex.Observable
import retrofit2.Retrofit

object WithFloatRepository : AppBaseRepository<WithFloatRemoteData, AppBaseLocalService>() {


  fun userAccountDetail(fpId: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.userAccountDetail(fpId, clientId),
      TaskCode.GET_ACCOUNT_DETAILS
    )
  }

  fun createAccount(request: AccountCreateRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createAccount(request), TaskCode.CREATE_ACCOUNT)
  }

  fun updateAccount(
    fpId: String?,
    clientId: String?,
    request: BankAccountDetailsN?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateAccount(fpId, clientId, request),
      TaskCode.UPDATE_ACCOUNT
    )
  }


  fun putUploadImage(request: UploadFileBusinessRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
     remoteDataSource.putUploadImage(
        clientId = request.clientId,
        fpId = request.fpId,
        identifierType = request.identifierType,
        fileName = request.fileName,
        requestBody = request.requestBody
      ), TaskCode.PUT_FILE_UPLOAD_IMAGE_PROFILE
    )
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatRemoteData> {
    return WithFloatRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}

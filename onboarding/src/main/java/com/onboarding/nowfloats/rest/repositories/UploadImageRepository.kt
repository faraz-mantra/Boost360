package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileProfileRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.WithFloatsApiClient
import com.onboarding.nowfloats.rest.services.remote.uploadfile.UploadImageRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object UploadImageRepository :
  AppBaseRepository<UploadImageRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<UploadImageRemoteDataSource> {
    return UploadImageRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun putUploadImageBusiness(request: UploadFileBusinessRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putUploadImageBusiness(
        request.clientId,
        request.fpId,
        request.identifierType,
        request.fileName,
        request.requestBody
      ), Taskcode.PUT_FILE_UPLOAD_BUSINESS
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
      ), Taskcode.PUT_FILE_UPLOAD_IMAGE_PROFILE
    )
  }

  fun putUploadSecondaryImage(request: UploadFileBusinessRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putUploadSecondaryImage(
        clientId = request.clientId,
        fpId = request.fpId,
        identifierType = request.identifierType,
        fileName = request.fileName,
        requestBody = request.requestBody
      ), Taskcode.PUT_FILE_UPLOAD_IMAGE_PROFILE
    )
  }

  fun putUploadImageProfile(request: UploadFileProfileRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putUploadImageProfile(
        request.clientId,
        request.loginId,
        request.fileName,
        request.requestBody
      ), Taskcode.PUT_FILE_UPLOAD_PROFILE
    )
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }

}
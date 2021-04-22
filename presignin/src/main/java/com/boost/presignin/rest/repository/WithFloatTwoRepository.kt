package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WithFloatsApiTwoClient
import com.boost.presignin.rest.services.remote.WithFloatTwoRemoteData
import com.boost.presignin.rest.userprofile.UserProfileRequest
import com.boost.presignin.rest.userprofile.UserProfileVerificationRequest
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, AppBaseLocalService>() {

  fun isMobileIsRegistered(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.checkIfMobileNumberIsRegistered(number, clientId = clientId), TaskCode.CHECK_MOBILE_IS_REGISTERED)
  }

  fun connectUserProfile(userProfileRequest: UserProfileRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.connectUserProfile(userProfileRequest), TaskCode.CONNECT_USER_PROFILE)
  }

  fun createUserProfile(userProfileRequest: UserProfileRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createUserProfile(userProfileRequest), TaskCode.CREATE_USER_PROFILE)
  }

  fun getFpDetailsByPhone(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFpDetailsByPhone(number, clientId = clientId), TaskCode.GET_FP_DETAILS_BY_PHONE)
  }

  fun sendOtpIndia(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.sendOtpIndia(number, clientId = clientId), TaskCode.SEND_OTP_INDIA)
  }

  fun verifyOtp(number: String?, otp: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.verifyOtp(number, otp, clientId = clientId), TaskCode.VERIFY_OTP)
  }

  fun getFpListForRegisteredNumber(number: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFpListForRegisteredMobile(number, clientId = clientId), TaskCode.GET_FP_LIST_FOR_REGISTERED_NUMBER)
  }

  fun verifyUserProfile(userProfileVerificationRequest: UserProfileVerificationRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.verifyUserProfile(userProfileVerificationRequest), TaskCode.VERIFY_USER_PROFILE)
  }


  fun getFpDetails(fpId: String, map: Map<String, String>): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFpDetails(fpId, map), TaskCode.GET_FP_USER_DETAILS)
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

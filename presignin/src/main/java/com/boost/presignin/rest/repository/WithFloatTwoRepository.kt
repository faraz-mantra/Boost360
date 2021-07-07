package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.login.ForgotPassRequest
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WithFloatsApiTwoClient
import com.boost.presignin.rest.services.remote.WithFloatTwoRemoteData
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import java.util.HashMap

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, AppBaseLocalService>() {

  fun isMobileIsRegistered(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.checkIfMobileNumberIsRegistered(
        number,
        clientId = clientId
      ), TaskCode.CHECK_MOBILE_IS_REGISTERED
    )
  }

  fun connectUserProfile(userProfileRequest: CreateProfileRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.connectUserProfile(userProfileRequest),
      TaskCode.CONNECT_USER_PROFILE
    )
  }

  fun createUserProfile(userProfileRequest: CreateProfileRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.createUserProfile(userProfileRequest),
      TaskCode.CREATE_USER_PROFILE
    )
  }

  fun getFpDetailsByPhone(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getFpDetailsByPhone(number, clientId = clientId),
      TaskCode.GET_FP_DETAILS_BY_PHONE
    )
  }

  fun sendOtpIndia(number: Long?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.sendOtpIndia(number, clientId = clientId),
      TaskCode.SEND_OTP_INDIA
    )
  }

  fun verifyOtp(number: String?, otp: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.verifyOtp(number, otp, clientId = clientId),
      TaskCode.VERIFY_OTP
    )
  }

  fun verifyLoginOtp(number: String?, otp: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.verifyLoginOtp(number, otp, clientId = clientId),
      TaskCode.VERIFY_LOGIN_OTP
    )
  }

  fun createAccessToken(request: AccessTokenRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.createAccessToken(request),
      TaskCode.ACCESS_TOKEN_CREATE
    )
  }


  fun getFpListForRegisteredNumber(number: String?, clientId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getFpListForRegisteredMobile(
        number,
        clientId = clientId
      ), TaskCode.GET_FP_LIST_FOR_REGISTERED_NUMBER
    )
  }

  fun verifyUserProfile(request: UserProfileVerificationRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.verifyUserProfile(request),
      TaskCode.VERIFY_USER_PROFILE
    )
  }

  fun forgotPassword(request: ForgotPassRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.forgotPassword(request), TaskCode.FORGOT_PASSWORD)
  }

  fun getFpDetails(fpId: String, map: Map<String, String>): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFpDetails(fpId, map), TaskCode.GET_FP_USER_DETAILS)
  }

  fun post_RegisterRia(@Body map: HashMap<String?, String?>?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.post_RegisterRia(map), TaskCode.REGISTER_RIA);
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

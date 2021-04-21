package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.FPDetailsResponse
import com.boost.presignin.model.FPListResponse
import com.boost.presignin.rest.EndPoints
import com.boost.presignin.rest.userprofile.*
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WithFloatTwoRemoteData {


    @Headers("Content-Type: application/json")
    @POST(EndPoints.CREATE_MERCHANT_PROFILE)
    fun createUserProfile(@Body userProfileRequest: UserProfileRequest): Observable<Response<UserProfileResponse>>

    @GET(EndPoints.CHECK_MOBILE_IS_REGISTERED)
    fun checkIfMobileNumberIsRegistered(@Query(value = "mobileNumber") mobileNumber: Long?, @Query(value = "clientId") clientId: String? = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"): Observable<Response<ResponseMobileIsRegistered>>

    @Headers("Content-Type: application/json")
    @POST(EndPoints.CONNECT_MERCHANT_AUTH_PROVIDER)
    fun connectUserProfile(@Body userProfileRequest: UserProfileRequest): Observable<Response<ConnectUserProfileResponse>>

    @Headers("Content-Type: application/json")
    @POST(EndPoints.VERIFY_LOGIN)
    fun verifyUserProfile(@Body userProfileVerificationRequest: UserProfileVerificationRequest): Observable<Response<VerificationRequestResult>>

    @Headers("Content-Type: application/json")
    @POST(EndPoints.VERIFY_LOGIN)
    fun verifyUserProfileAny(@Body userProfileVerificationRequest: UserProfileVerificationRequest): Observable<Response<UserProfileResponse>>

    @GET(EndPoints.GET_FP_DETAILS_BY_PHONE)
    fun getFpDetailsByPhone(@Query("number") number: Long?, @Query(value = "clientId") clientId: String? = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"): Observable<Response<FPDetailsResponse>>

    @GET(EndPoints.SEND_OTP_INDIA)
    fun sendOtpIndia(@Query("mobileNumber") number: Long?, @Query("messageTemplate") messageTemplate: String? = "Your Boost 360 verification code is [OTP] . Code valid for 10 minutes only, one-time use. Please DO NOT share this OTP with anyone to ensure account's security.", @Query(value = "clientId") clientId: String? = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"): Observable<Response<ResponseBody>>

    @GET(EndPoints.VERIFY_OTP)
    fun verifyOtp(@Query("mobileNumber") number: String?, @Query("otp") otp: String?,
                  @Query(value = "clientId") clientId: String? = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"): Observable<Response<ResponseBody>>

    @GET(EndPoints.FP_LIST_REGISTERED_MOBILE)
    fun getFpListForRegisteredMobile(@Query("mobileNumber") number: String?, @Query(value = "clientId") clientId: String? = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"): Observable<Response<FPListResponse>>

    @GET(EndPoints.GET_FP_DETAILS)
    fun getFpDetails(@Path("fpid")  fpid:String,@QueryMap  map:Map<String,String>): Observable<Response<FPDetailsResponse>>
}

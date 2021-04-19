package com.boost.presignin.rest.services.remote

import com.boost.presignin.rest.EndPoints
import com.boost.presignin.rest.userprofile.*
import io.reactivex.Observable
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

}
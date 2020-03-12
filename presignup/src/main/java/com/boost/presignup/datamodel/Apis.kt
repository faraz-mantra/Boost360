package com.boost.presignup.datamodel

import com.boost.presignup.datamodel.userprofile.*
import retrofit2.Call
import retrofit2.http.*

interface Apis{
    @Headers("Content-Type: application/json")
    @POST("/user/v9/floatingPoint/CreateMerchantProfile")
    fun createUserProfile(@Body userProfileRequest: UserProfileRequest): Call<UserProfileResponse>

    @Headers("Content-Type: application/json")
    @POST("/user/v9/floatingPoint/ConnectMerchantAuthProvider")
    fun connectUserProfile(@Body userProfileRequest: UserProfileRequest): Call<ConnectUserProfileResponse>

    @Headers("Content-Type: application/json")
    @POST("/discover/v1/floatingPoint/verifyLogin")
    fun verifyUserProfile(@Body userProfileVerificationRequest: UserProfileVerificationRequest): Call<UserProfileVerificationResponse>
}
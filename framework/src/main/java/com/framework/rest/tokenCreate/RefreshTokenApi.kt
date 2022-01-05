package com.framework.rest.tokenCreate

import com.framework.rest.EndPoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenApi {

  @POST(EndPoints.CREATE_TOKEN)
 suspend fun createAccessToken(@Body request: AccessTokenRequest?): Response<AccessTokenResponse>
}
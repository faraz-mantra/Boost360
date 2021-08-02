package com.onboarding.nowfloats.rest.services.remote.google

import com.onboarding.nowfloats.model.googleAuth.FirebaseTokenResponse
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface GoogleAuthRemoteDataSource {
  @Headers("Accept: application/json", "Content-Type: application/x-www-form-urlencoded")
  @POST(EndPoints.POST_GOOGLE_AUTH_TOKENS)
  fun getGoogleAuthToken(
    @Query("client_id") client_id: String? = null,
    @Query("client_secret") client_secret: String? = null,
    @Query("code") auth_code: String? = null,
    @Query("grant_type") grant_type: String? = "authorization_code",
    @Query("redirect_uri") redirect_uri: String? = ""
  ): Observable<Response<GoogleAuthResponse>>

  @GET
  fun getFirebaseToken(
    @Url url: String=EndPoints.GET_FIREBASE_TOKEN,
    @Query("clientId") client_id: String?
  ):Observable<Response<FirebaseTokenResponse>>

}
package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthTokenRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.GoogleAuthApiClient
import com.onboarding.nowfloats.rest.services.remote.google.GoogleAuthRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object GoogleAuthRepository : AppBaseRepository<GoogleAuthRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<GoogleAuthRemoteDataSource> {
    return GoogleAuthRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getGoogleAuthToken(req: GoogleAuthTokenRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getGoogleAuthToken(
        req?.client_id,
        req?.client_secret,
        req?.auth_code,
        req?.grant_type,
        req?.redirect_uri
      ), Taskcode.POST_GOOGLE_AUTH_TOKEN
    )
  }

  override fun getApiClient(): Retrofit {
    return GoogleAuthApiClient.shared.retrofit
  }
}
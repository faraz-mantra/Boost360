package com.onboarding.nowfloats.rest.services.remote.feature

import com.onboarding.nowfloats.rest.response.feature.FeatureListResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface FeatureRemoteDataSource {

  @GET("v2/5e68921d2f00000842d8ad84")
  fun getFeatures(): Observable<Response<FeatureListResponse>>

}
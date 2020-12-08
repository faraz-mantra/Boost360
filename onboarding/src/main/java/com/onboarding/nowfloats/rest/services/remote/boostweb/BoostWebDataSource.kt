package com.onboarding.nowfloats.rest.services.remote.boostweb

import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BoostWebDataSource {

  @FormUrlEncoded
  @POST(EndPoints.MERCHANT_PROFILE)
  fun getMerchantProfile(@Field("floatingpointId") floatingpointId: String?): Observable<Response<MerchantProfileResponse>>


}
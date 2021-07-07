package com.onboarding.nowfloats.rest.services.remote.riaWhatsapp

import com.onboarding.nowfloats.model.riaWhatsapp.RiaWhatsappRequest
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RiaWhatsappRemoteDataSource {

  @POST(EndPoints.RIA_WHATSAPP)
  fun updateRiaWhatsapp(
    @Query("authClientId") authClientId: String?,
    @Body request: RiaWhatsappRequest?
  ): Observable<Response<Any>>

}
package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.other.PaymentKycDataResponse
import com.boost.presignin.rest.EndPoints
import com.framework.base.BaseResponse
import com.framework.pref.clientId2
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface RiaWithFloatDataSource {

  @POST(value = EndPoints.WHATSAPP_OPT_IN)
  fun whatsappOptIn(
    @Query("authClientId") clientId:String= clientId2,
    @Body body: JsonObject
  ): Observable<Response<Any>>
}
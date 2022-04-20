package com.boost.presignin.rest.services.remote

import com.boost.presignin.rest.EndPoints
import com.framework.pref.clientId2
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RiaWithFloatDataSource {

  @POST(value = EndPoints.WHATSAPP_OPT_IN)
  fun whatsappOptIn(
    @Query("authClientId") clientId:String= clientId2,
    @Body body: JsonObject
  ): Observable<Response<Any>>
}
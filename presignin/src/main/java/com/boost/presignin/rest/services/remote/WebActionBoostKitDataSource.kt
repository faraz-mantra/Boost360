package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.other.PaymentKycDataResponse
import com.boost.presignin.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WebActionBoostKitDataSource {

    @Headers("Authorization: 597ee93f5d64370820a6127c", "Accept: application/json")
    @GET(value = EndPoints.GET_DATA)
    fun getSelfBrandedKyc(@Query("query") query: String?): Observable<Response<PaymentKycDataResponse>>
}
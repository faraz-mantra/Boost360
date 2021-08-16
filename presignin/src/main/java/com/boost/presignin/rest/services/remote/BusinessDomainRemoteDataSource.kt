package com.boost.presignin.rest.services.remote

import BusinessDomainRequest
import com.boost.presignin.model.businessdomain.BusinessDomainSuggestRequest
import com.boost.presignin.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BusinessDomainRemoteDataSource {

  @POST(EndPoints.POST_BUSINESS_DOMAIN_URL)
  fun checkBusinessDomain(@Body request: BusinessDomainRequest): Observable<Response<String>>

  @POST(EndPoints.POST_BUSINESS_DOMAIN_SUGGEST)
  fun checkBusinessDomainSuggest(@Body request: BusinessDomainSuggestRequest): Observable<Response<String>>
}
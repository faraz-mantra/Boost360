package com.onboarding.nowfloats.rest.services.remote.domain

import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainSuggestRequest
import com.onboarding.nowfloats.rest.EndPoints
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
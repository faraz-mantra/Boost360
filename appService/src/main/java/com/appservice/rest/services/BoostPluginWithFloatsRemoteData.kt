package com.appservice.rest.services

import com.appservice.model.account.testimonial.webActionList.TestimonialWebActionResponse
import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BoostPluginWithFloatsRemoteData {

    @GET(EndPoints.DOMAIN_DETAILS)
    fun getDomainDetails(
        @Path("fpTag") fpTag: String?,
        @Query("clientId") clientId: String?
    ): Observable<Response<DomainDetailsResponse>>

}
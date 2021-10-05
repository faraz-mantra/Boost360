package com.appservice.rest.services

import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.rest.EndPoints
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface BoostPluginWithFloatsRemoteData {

    @GET(EndPoints.DOMAIN_DETAILS)
    fun getDomainDetails(
        @Path("fpTag") fpTag: String?,
        @Query("clientId") clientId: String?
    ): Observable<Response<DomainDetailsResponse>>

    @GET(EndPoints.SEARCH_DOMAIN)
    fun getSearchDomain(
        @Path("domain") domain: String,
        @Query("clientId") clientId: String,
        @Query("domainType") domainType: String
    ): Observable<Response<Boolean>>

    @POST(EndPoints.CREATE_DOMAIN)
    fun createDomain(
       @Query("clientId") clientId: String,
       @Query("domainName") domainName: String,
       @Query("domainType") domainType: String,
       @Query("existingFPTag") existingFPTag: String,
       @Query("domainChannelType") domainChannelType: Int = 1,
       @Query("DomainRegService") DomainRegService: Int = 0,
       @Query("validityInYears") validityInYears: String,
       @Query("DomainOrderType") domainOrderType: Int
    ): Observable<Response<BaseResponse>>

}
package com.inventoryorder.rest.services

import com.inventoryorder.model.badges.BadgeCountResponse
import com.inventoryorder.model.mapDetail.VisitsModelResponse
import com.inventoryorder.model.spaAppointment.GetServiceListingResponse
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WithFloatDataSource {

  @GET(EndPoints.GET_USER_SUMMARY_FILTER)
  fun getUserSummary(
    @Path("fpTag") fpTag: String?,
    @Query("clientId") clientId: String?,
    @Query("fpId") fpIdParent: String?,
    @Query("scope") scope: String? = "0", //enterprise for 1
    @Query("startDate") startDate: String?,
    @Query("endDate") endDate: String?
  ): Observable<Response<UserSummaryResponse>>

  @GET(EndPoints.GET_USER_MESSAGE_COUNT_FILTER)
  fun getUserMessageCount(
    @Path("fpId") fpIdParent: String?,
    @Query("clientId") clientId: String?,
    @Query("startDate") startDate: String?,
    @Query("endDate") endDate: String?
  ): Observable<Response<Any>>

  @GET(EndPoints.GET_SUBSCRIBER_COUNT_FILTER)
  fun getSubscriberCount(
    @Path("fpTag") fpTag: String?,
    @Query("clientId") clientId: String?,
    @Query("startDate") startDate: String?,
    @Query("endDate") endDate: String?
  ): Observable<Response<Any>>

  @GET(EndPoints.GET_USER_CALL_SUMMARY_FILTER)
  fun getUserCallSummary(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpIdParent: String?,
    @Query("identifierType") identifierType: String? = "SINGLE",
    @Query("startDate") startDate: String?,
    @Query("endDate") endDate: String?
  ): Observable<Response<CallSummaryResponse>>

  @GET(EndPoints.GET_MAP_ADDRESS_DETAILS_FILTER)
  fun getMapVisits(
    @Path("fpTag") fpTag: String?,
    @QueryMap mapData: Map<String, String>?,
  ): Observable<Response<VisitsModelResponse>>
}
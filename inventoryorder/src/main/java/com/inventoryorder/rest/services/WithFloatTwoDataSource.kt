package com.inventoryorder.rest.services

import com.inventoryorder.model.floatMessage.MessageModel
import com.inventoryorder.model.mapDetail.VisitsModelResponse
import com.inventoryorder.model.services.InventoryServicesResponseItem
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WithFloatTwoDataSource {

  @GET(EndPoints.GET_LIST_INVENTORY_SYNC)
  fun getAllServiceList(
      @Query("clientId") clientId: String?,
      @Query("skipBy") skipBy: Int?,
      @Query("fpTag") fpTag: String?,
      @Query("identifierType") identifierType: String?,
  ): Observable<Response<Array<InventoryServicesResponseItem>>>

  @GET(EndPoints.SEND_SMS)
  fun sendSMS(
      @Query("mobileNumber", encoded = true) mobile: String?,
      @Query("message") message: String?,
      @Query("clientId") clientId: String?,
  ): Observable<Response<Void>>

  @GET(EndPoints.GET_BIZ_FLOATS_MESSAGE)
  fun getBizFloatMessage(@QueryMap map: Map<String, String>): Observable<Response<MessageModel>>

  @GET(EndPoints.GET_USER_SUMMARY)
  fun getUserSummary(
      @Query("clientId") clientId: String?,
      @Query("fpId") fpIdParent: String?,
      @Query("scope") scope: String? = "0", //enterprise for 1
  ): Observable<Response<UserSummaryResponse>>

  @GET(EndPoints.GET_MAP_ADDRESS_DETAILS)
  fun getMapVisits(
      @Path("fpTag") fpTag: String?,
      @QueryMap mapData: Map<String, String>?,
  ): Observable<Response<VisitsModelResponse>>

  @GET(EndPoints.GET_USER_CALL_SUMMARY)
  fun getUserCallSummary(
      @Query("clientId") clientId: String?,
      @Query("fpId") fpIdParent: String?,
      @Query("identifierType") identifierType: String? = "SINGLE",
  ): Observable<Response<CallSummaryResponse>>
}
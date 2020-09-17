package com.inventoryorder.rest.services

import com.inventoryorder.model.services.InventoryServicesResponseItem
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WithFloatTwoDataSource {

    @GET(EndPoints.GET_LIST_INVENTORY_SYNC)
    fun getAllServiceList(
            @Query("clientId") clientId: String?,
            @Query("skipBy") skipBy: Int?,
            @Query("fpTag") fpTag: String?,
            @Query("identifierType") identifierType: String?
    ): Observable<Response<Array<InventoryServicesResponseItem>>>

    @GET(EndPoints.SEND_SMS)
    fun sendSMS(
        @Query("mobileNumber", encoded = true) mobile: String?,
        @Query("message") message: String?,
        @Query("clientId") clientId: String?
    ): Observable<Response<Void>>

}
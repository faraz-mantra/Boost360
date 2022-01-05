package com.framework.rest.errorTicketGenerate

import com.framework.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SalesAssignRemoteData {

    @GET(EndPoints.CREATE_ERROR_TICKET)
    fun createErrorTicket(@Query("fpTag") fpTag :String, @Query("subject") subject :String,
                          @Query("comment") comment :String, @Query("submitterEmail") submitterEmail :String) : Observable<Response<Any>>
}
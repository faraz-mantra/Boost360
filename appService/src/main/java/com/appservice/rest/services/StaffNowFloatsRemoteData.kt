package com.appservice.rest.services

import com.appservice.rest.EndPoints
import com.appservice.staffs.model.ServiceListRequest
import com.appservice.staffs.model.ServiceListResponse
import com.appservice.staffs.model.StaffCreateProfileRequest
import com.appservice.staffs.model.StaffCreateProfileResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StaffNowFloatsRemoteData {
    @POST(EndPoints.CREATE_STAFF_PROFILE)
    fun createStaffProfile(@Body request: StaffCreateProfileRequest?): Observable<Response<StaffCreateProfileResponse>>

    @POST(EndPoints.FETCH_STAFF_SERVICES)
    fun fetchServices(@Body request: ServiceListRequest?): Observable<Response<ServiceListResponse>>

}